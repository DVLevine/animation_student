package cs4620.shape;

import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;

public class Sphere extends TriangleMesh {

	private static float DEFAULT_RADIUS = 1.00f;

	public Sphere(GL2 gl) {
		super(gl);
	}

	private void sphereVertex(float r, int div, int latIndex, int longIndex, 
			float[] vertices, float[] normals, float[] texCoords, int pos)
	{
		// using spherical coordinates (r, theta, phi)
		// phi only multiplied by pi (not 2pi) to include all divisions
		// in semicircle. Otherwise, angles could be symmetrical over
		// 2pi, which will result in drawing only 1/2 the required lines

		float theta = (float) (longIndex*1.0f/div*2*Math.PI);	// longitude
		float phi = (float) (latIndex*1.0f/div*Math.PI);	// latitude

		float cosTheta = (float)Math.cos(theta);
		float sinTheta = (float)Math.sin(theta);
		float cosPhi = (float)Math.cos(phi);
		float sinPhi = (float)Math.sin(phi);

		// the y-coordinate uses cosPhi to include all +/- angles in [0, pi]
		// draw the horizontal circles (around y-axis) from top down
		vertices[3*pos]   = r*sinPhi*cosTheta;
		vertices[3*pos+1] = r*cosPhi; 
		vertices[3*pos+2] = r*sinPhi*sinTheta;

		// set texture coords - flip around x-axis for non-mirrored map
		texCoords[2*pos] =   (1.0f)-longIndex*1.0f/div;	// u coordinate
		texCoords[2*pos+1] = (1.0f)-latIndex*1.0f/div; 	// v coordinate

		normals[3*pos]   = sinPhi*cosTheta;
		normals[3*pos+1] = cosPhi; 
		normals[3*pos+2] = sinPhi*sinTheta;
	}

	@Override
	public void buildMesh(GL2 gl, float tolerance)
	{
		// TODO (Scene P2): Implement mesh generation for Sphere. Your code should
		// fill arrays of vertex positions/normals and vertex indices for triangles/lines
		// and put this information in the GL buffers using the
		//   set*()
		// methods from TriangleMesh.

		int div = (int)Math.ceil(4*Math.PI*DEFAULT_RADIUS / tolerance);

		// need to pull bottom of sphere (lowest y-coord) to point
		// so include extra row of points by using div + 1

		int vertexCount = (div+1) * (div+1);
		float[] vertices = new float[3*vertexCount];
		float[] normals = new float[3*vertexCount];
		float[] texCoords = new float[2*vertexCount];

		for(int i0 = 0; i0 <= div; i0++)
			for(int j0 = 0; j0 < div; j0++)
				sphereVertex(DEFAULT_RADIUS, div, i0, j0, 
						vertices, normals, texCoords, i0*(div+1)+j0);

		duplicateVertices(div, vertices, normals, texCoords);

		// generate triangle mesh
		// traverse points in a triangle in ccw direction
		// i0 represents horizontal (y-axis coords)
		// j0 represents longitudinal divisions in x-z plane

		int triangleCount = (div+1) * div * 2;
		int[] triangles = new int[3*triangleCount];
		for(int i0 = 0; i0 < div; i0++)
		{
			int i1 = (i0+1); // i1 will reach extra line at bottom of sphere
			for(int j0 = 0; j0 < div; j0++)
			{
				int j1 = (j0+1); // j1 will reach duplicated vertex

				triangles[6*(i0*div+j0)  ] = i0*(div+1)+j0;
				triangles[6*(i0*div+j0)+1] = i1*(div+1)+j1;
				triangles[6*(i0*div+j0)+2] = i1*(div+1)+j0;

				triangles[6*(i0*div+j0)+3] = i0*(div+1)+j0;
				triangles[6*(i0*div+j0)+4] = i0*(div+1)+j1;
				triangles[6*(i0*div+j0)+5] = i1*(div+1)+j1;
			}
		}

		// wire mesh generation
		// connect all horizontal (along y-axis) lines in lines[0, 2*(div+1)*div)
		// connect all vertical lines in lines[2*(div+1)*div, 4*(div+1)*div)
		int[] lines = new int[4 * (div+1) * div];
		int toSmall = 2 * (div+1) * div; // index of second set of lines (vertical)
		for(int i0 = 0; i0 < div; i0++)
		{
			int i1 = (i0+1); // i1 will reach extra line at bottom of sphere
			for(int j0 = 0; j0 < div; j0++)
			{
				int j1 = (j0+1); // j1 will reach duplicated vertex

				lines[2 * (i0 * div + j0) + 0] = i0 * (div+1) + j0;
				lines[2 * (i0 * div + j0) + 1] = i0 * (div+1) + j1;
				lines[toSmall + 2 * (i0 * div + j0) + 0] = i0 * (div+1) + j0;
				lines[toSmall + 2 * (i0 * div + j0) + 1] = i1 * (div+1) + j0;
			}
		}

		setVertices(gl, vertices);
		setNormals(gl, normals);
		setTriangleIndices(gl, triangles);
		setWireframeIndices(gl, lines);

		// TODO (Shaders 2 P2): Generate texture coordinates for the sphere also.
		setTexCoords(gl, texCoords);	//set vertex array for texture
	}
	
	private void duplicateVertices(int div, float[] vertices, float[] normals, float[] texCoords)
	{
		// duplicate vertices for vertical line down the sphere
		for (int i = 0; i <= div; i++) {
			int posBeg = i * (div+1);		// pos at the beginning of each row
			int posEnd = i * (div+1) + div;	// pos at the end of each row
			
			vertices[3*posEnd]   = vertices[3*posBeg];
			vertices[3*posEnd+1] = vertices[3*posBeg+1]; 
			vertices[3*posEnd+2] = vertices[3*posBeg+2];

			// set u to 0 (naturally at 1) to prevent duplicating entire texture again
			texCoords[2*posEnd] =   0;						// u coordinate
			texCoords[2*posEnd+1] = texCoords[2*posBeg+1];	// v coordinate

			normals[3*posEnd]   = normals[3*posBeg];
			normals[3*posEnd+1] = normals[3*posBeg+1]; 
			normals[3*posEnd+2] = normals[3*posBeg+2];
		}
	}

	@Override
	public Object getYamlObjectRepresentation()
	{
		Map<Object,Object> result = new HashMap<Object, Object>();
		result.put("type", "Sphere");
		return result;
	}


}
