package cs4620.spline;

import javax.media.opengl.GL2;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import cs4620.framework.Transforms;
import cs4620.shape.TriangleMesh;

/*
 * A revolution volume. Generates a mesh by revolving its control curve
 * around the y axis.
 */
public class RevolutionVolume extends TriangleMesh {
    protected DiscreteCurve curve;

    public RevolutionVolume(GL2 gl, DiscreteCurve curve) {
        super(gl);
        this.curve = curve;
    }
    
    private void rvVertex(float fv_x, float fv_y, float nv_x, float nv_y, float length, int divU, int divV, 
    		int uIndex, int vIndex, float[] vertices, float[] normals, float[] texCoords, int pos)
	{
		float phi = (float) (uIndex*1.0f/divU*2*Math.PI);

		float cosPhi = (float)Math.cos(phi);
		float sinPhi = (float)Math.sin(phi);

		vertices[3*pos]   = fv_x*cosPhi;
		vertices[3*pos+1] = fv_y;
		vertices[3*pos+2] = -fv_x*sinPhi;
		
		texCoords[2*pos] =   uIndex*1.0f/divU;	// texture u-coordinate
		texCoords[2*pos+1] = 1.0f - length; 	// texture v-coordinate
		
		normals[3*pos]   = nv_x*cosPhi;
		normals[3*pos+1] = nv_y;
		normals[3*pos+2] = -nv_x*sinPhi;
	}
    
    // duplicate the vertices along the original 2D spline (z=0)
    private void duplicateVertices(int divU, int divV, float[] vertices, float[] normals, float[] texCoords)
	{
		// duplicate vertices for vertical line down the sphere
		for (int i = 0; i < divV ; i++) {
			int posBeg = i * (divU+1);			// pos at the beginning of each revolution
			int posEnd = i * (divU+1) + divU;	// pos at the end of each revolution
			
			vertices[3*posEnd]   = vertices[3*posBeg];
			vertices[3*posEnd+1] = vertices[3*posBeg+1]; 
			vertices[3*posEnd+2] = vertices[3*posBeg+2];

			// set texture coordinate u to 0
			texCoords[2*posEnd] =   1;						// u coordinate
			texCoords[2*posEnd+1] = texCoords[2*posBeg+1];	// v coordinate

			normals[3*posEnd]   = normals[3*posBeg];
			normals[3*posEnd+1] = normals[3*posBeg+1]; 
			normals[3*posEnd+2] = normals[3*posBeg+2];
		}
	}

    @Override
    public void buildMesh(GL2 gl, float tolerance) {
        // TODO (Splines Problem 1, Section 3.4):
    	// Compute the positions, normals, and texture coordinates for the surface
    	// of revolution mesh. Start with the vertices in the DiscreteCurve.
    	
    	int divU = (int) Math.ceil(4 * Math.PI / tolerance);
    	int divV = (curve.vertices.length / 2);
    	
    	int vertexCount = (divU+1) * divV;
		float[] vertices = new float[3*vertexCount];
		float[] normals = new float[3*vertexCount];
		float[] texCoords = new float[2*vertexCount];
		
		float[] length_buffer = ((BSpline)curve).getLengthBuffer();
		
		float fv_x, fv_y, nv_x, nv_y;
		for(int v0 = 0; v0 < divV; v0++) {
			fv_x = curve.vertices[2*v0];
			fv_y = curve.vertices[2*v0+1];
			nv_x = curve.normals[2*v0];
			nv_y = curve.normals[2*v0+1];
			for(int u0 = 0; u0 < (divU+1); u0++) {
				rvVertex(fv_x, fv_y, nv_x, nv_y, length_buffer[v0], divU, divV, u0, v0,
						vertices, normals, texCoords, v0*(divU+1)+u0);
			}
		}
		
		//duplicateVertices(divU, divV, vertices, normals, texCoords);
		
		int triangleCount = divU * divV * 2;
		int[] triangles = new int[3*triangleCount];
		for(int v0 = 0; v0 < divV-1; v0++)
		{
			int v1 = (v0+1);
			for(int u0 = 0; u0 < divU; u0++)
			{
				int u1 = (u0+1);

				triangles[6*(v0*divU+u0)  ] = (v0*(divU+1))+u0;
				triangles[6*(v0*divU+u0)+1] = (v1*(divU+1))+u0;
				triangles[6*(v0*divU+u0)+2] = (v0*(divU+1))+u1;

				triangles[6*(v0*divU+u0)+3] = (v1*(divU+1))+u0;
				triangles[6*(v0*divU+u0)+4] = (v1*(divU+1))+u1;
				triangles[6*(v0*divU+u0)+5] = (v0*(divU+1))+u1;
			}
		}
		
		int[] lines = new int[4 * divU * divV];
		int toSmall = 2 * divU * divV; // index of second set of lines (vertical)
		for(int v0 = 0; v0 < divV-1; v0++)
		{
			int v1 = (v0+1);
			for(int u0 = 0; u0 < divU; u0++)
			{
				int u1 = (u0+1);

				lines[2 * (v0 * divU + u0) + 0] = v0 * (divU+1) + u0;
				lines[2 * (v0 * divU + u0) + 1] = v0 * (divU+1) + u1;
				lines[toSmall + 2 * (v0 * divU + u0) + 0] = v0 * (divU+1) + u0;
				lines[toSmall + 2 * (v0 * divU + u0) + 1] = v1 * (divU+1) + u0;
			}
		}
		// connect vertices in the very last row (horizontal)
		for (int u0 = 0; u0 < divU; u0++) {
			int u1 = (u0+1);
			lines[2 * ((divV-1) * divU + u0) + 0] = (divV-1) * (divU+1) + u0;
			lines[2 * ((divV-1) * divU + u0) + 1] = (divV-1) * (divU+1) + u1;
		}
		
		setVertices(gl, vertices);
		setNormals(gl, normals);
		setTriangleIndices(gl, triangles);
		setWireframeIndices(gl, lines);

		setTexCoords(gl, texCoords);	//set vertex array for texture
			
    }

	@Override
	public Object getYamlObjectRepresentation() {
		return null;
	}
}
