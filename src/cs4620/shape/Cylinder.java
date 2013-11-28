package cs4620.shape;

import java.util.HashMap;
import java.util.Map;

import javax.media.opengl.GL2;

public class Cylinder extends TriangleMesh
{
	private static float DEFAULT_RADIUS = 1.00f;

	public Cylinder(GL2 gl)
	{
		super(gl);
	}

	private void cylinderVertex(float r,int div,int thetaIndex, 
			float[] vertices, float[] normals, float[] texCoords, int pos)
	{
		float theta = (float) (thetaIndex*(1.0f/div)*2*Math.PI);	
		float z = (float) (Math.pow(-1.0,(pos*1.0f)));
		float cosTheta = (float)Math.cos(theta);
		float sinTheta = (float)Math.sin(theta);

		vertices[3*pos]   = r*cosTheta;
		vertices[3*pos+1] = z; 
		vertices[3*pos+2] = r*sinTheta;

		normals[3*pos]   = cosTheta;
		normals[3*pos+1] = 0;
		normals[3*pos+2] = sinTheta;

		texCoords[2*pos] = (1.0f)-(thetaIndex*1.0f/div); //u coordinate
		texCoords[2*pos+1] = ((z+1)/2.0f)*1.0f; //v coordinates of cylinder

	}

	private void capVertex(float capHeight, float r, int div, int thetaIndex, 
			float[] vertices, float[] normals,float[] texCoords, int pos){
		float theta = (float) (thetaIndex*(1.0f/div)*2*Math.PI);	
		float z = capHeight;
		float cosTheta = (float)Math.cos(theta);
		float sinTheta = (float)Math.sin(theta);

		vertices[3*pos]   = r*cosTheta;
		vertices[3*pos+1] = z; 
		vertices[3*pos+2] = r*sinTheta;

		normals[3*pos]   = 0; // cooler shading - (float)0.25*cosTheta;
		normals[3*pos+1] = z;
		normals[3*pos+2] = 0; // cooler shading - (float)0.25*sinTheta;

		float cosTexTheta = (float)Math.cos(theta+Math.PI);
		float sinTexTheta = (float)Math.sin(theta+Math.PI);

		texCoords[2*pos] = (r*cosTexTheta+1)/2;
		if (z>0){
			texCoords[2*pos+1] = (r*sinTheta+1)/2;
		}
		else{
			texCoords[2*pos+1] = (r*-sinTheta+1)/2;
		}
	}

	@Override
	public void buildMesh(GL2 gl, float tolerance)
	{
		// TODO (Scene P2): Implement mesh generation for Cylinder. Your code should
		// fill arrays of vertex positions/normals and vertex indices for triangles/lines
		// and put this information in the GL buffers using the
		//   set*()
		// methods from TriangleMesh.

		int div = (int) Math.ceil(4*Math.PI*DEFAULT_RADIUS / tolerance);

		int vertexCount = 4*div;
		float[] vertices = new float [3*vertexCount];
		float[] normals = new float [3*vertexCount];
		float[] texCoords =new float [2*vertexCount];

		//going through parts of angles. Generating vertices.

		//bottom cap
		for (int i0=2*div; i0<3*div; i0++){
			capVertex(-1,DEFAULT_RADIUS,div,i0,vertices,normals,texCoords, i0);
		}

		//top cap
		for (int i0=3*div; i0<4*div;i0++){
			capVertex(1,DEFAULT_RADIUS,div,i0,vertices,normals,texCoords, i0);
		}

		//cylinder body
		for (int e0=0; e0<div; e0++){
			for (int j0=0; j0<2; j0++){
				cylinderVertex(DEFAULT_RADIUS,div,e0,vertices,normals,texCoords, e0*2+j0);
			}
		}


		int triangleCount = (div*2) * 8;
		int[] triangles = new int[3*triangleCount];

		//Bottom Cap - assigning vertices to triangles
		for(int i0=2*div;i0<3*div;i0++)
		{
			int i1 = (i0+1) % div + 2*div;

			triangles[6*(i0*1) ] = i0*1;
			triangles[6*(i0*1)+1] = i1*1;
			triangles[6*(i0*1)+2] = 2*div;

			triangles[6*(i0*1)+3] = 2*div;
			triangles[6*(i0*1)+4] = i1*1;
			triangles[6*(i0*1)+5] = i0*1;

		}

		//Main Body - assigning vertices to triangles
		for (int i0=0; i0<div;i0++){
			int i1 = (i0+1) % div ;
			for(int j0=0;j0<2;j0++)
			{
				int j1 = (j0+1) % 2;

				triangles[6*(i0*2+j0)  ] = i0*2+j0;
				triangles[6*(i0*2+j0)+1] = i1*2+j0;
				triangles[6*(i0*2+j0)+2] = i1*2+j1;

				triangles[6*(i0*2+j0)+3] = i0*2+j0;
				triangles[6*(i0*2+j0)+4] = i1*2+j1;
				triangles[6*(i0*2+j0)+5] = i0*2+j1;
			}
		}


		//Top Cap - assigning vertices to triangles
		for(int i0=3*div;i0<4*div;i0++)
		{
			int i1 = (i0+1) % div+(3*div);


			triangles[6*(i0*1) ] = i0*1;
			triangles[6*(i0*1)+1] = i1*1;
			triangles[6*(i0*1)+2] = 3*div;

			triangles[6*(i0*1)+3] = 3*div;
			triangles[6*(i0*1)+4] = i1*1;
			triangles[6*(i0*1)+5] = i0*1;

		}

		int[] lines = new int[4 * (4*div)];
		int toSmall = 2 * (4*div);
		for(int i0=0;i0<div;i0++)
		{
			int i1 = (i0+1) % div;
			for(int j0=0;j0<2;j0++)
			{
				int j1 = (j0+1) % 2;

				lines[2 * (j0 * div + i0) + 0] = i0*2+j0;
				lines[2 * (j0 * div + i0) + 1] = i1*2+j0;
				lines[toSmall + 2 * (i0 * 2 + j0) + 0] = i0*2+j0;
				lines[toSmall + 2 * (i0 * 2 + j0) + 1] = i0*2+j1;
			}
		}

		for(int i0=2*div;i0<3*div;i0++)
		{
			lines[2*(i0)] = i0;
			lines[2*(i0)+1] = 2*div;
			lines[toSmall + 2*(i0)+0] = 2*div;
			lines[toSmall + 2*(i0)+1] = i0;
		}

		for(int i0=3*div;i0<4*div;i0++)
		{
			lines[2*(i0)] = i0;
			lines[2*(i0)+1] = 3*div;
			lines[toSmall + 2*(i0)+0] = 3*div;
			lines[toSmall + 2*(i0)+1] = i0;
		}

		setVertices(gl, vertices);
		setNormals(gl, normals);
		setTriangleIndices(gl, triangles);
		setWireframeIndices(gl, lines);

		// TODO (Shaders 2 P2): Generate texture coordinates for the cylinder also
		setTexCoords(gl, texCoords);

	}


	@Override
	public Object getYamlObjectRepresentation()
	{
		Map<Object,Object> result = new HashMap<Object, Object>();
		result.put("type", "Cylinder");
		return result;
	}
}
