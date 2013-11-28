package cs4620.spline;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.media.opengl.GL2;
import javax.vecmath.GMatrix;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;

import org.yaml.snakeyaml.Yaml;

import cs4620.util.Util;

/*
 * The BSpline class. Takes a set of control points and produces
 * a set of vertices along the smooth curve based on an epsilon
 * criteria. Also computes the accumulated length of the curve at
 * each vertex, which is used for animation.
 */
public class BSpline extends DiscreteCurve {
	protected boolean isClosed;

	protected Vector2f[] bezier = null;
	protected ArrayList<Vector2f> localControlPoints = new ArrayList<Vector2f>();
	protected float [] length_buffer;
	private float totLength = 0.0f;

	// Workspaces for spline tesselation routines
	// (caution, not a thread safe programming practice)
	private Vector2f[] bspPoints = new Vector2f[4];
	private Vector2f[] bezPoints = new Vector2f[4];

	public BSpline(GL2 gl, boolean isClosed, float canvasSize) {
		super(gl, canvasSize);
		this.isClosed = isClosed;
		for (int i = 0; i < 4; i++) {
			bspPoints[i] = new Vector2f();
			bezPoints[i] = new Vector2f();
		}
	}

	public ArrayList<Vector2f> getCtrlPts() {
		return localControlPoints;
	}

	public void setCtrlPts( ArrayList<Vector2f> ctrlPts) {
		localControlPoints = ctrlPts;
	}

	public boolean isClosed()
	{
		return isClosed;
	}

	public void setClosed(boolean value)
	{
		isClosed = value;
	}

	public float getTotalLength() {
		return totLength;
	}

	public float[] getLengthBuffer() {
		return length_buffer;
	}


	/*
	 * Approximate a Bezier segment with a number of vertices, according to an appropriate
	 * criterion for how many are needed.  The points on the curve are written into the
	 * array outPoints, and the corresponding tangent vectors (not normalized) are written
	 * into outTangents.  The last point (the 4th control point) is not included.
	 */
	//	public void tessellate_bezier(Vector2f cp[], float epsilon, ArrayList<Vector2f> outPoints, ArrayList<Vector2f> outDerivs) {
	//		// TODO (Splines P1): Tesselate a bezier segment
	//		
	//		// choose evenly spaced u values for deCasteljau's
	//		float[] uValues = {0.25f, 0.5f, 0.75f};
	//		
	//		// add p0 and deriv @ p0 to lists
	//		Vector2f p0Tangent = new Vector2f();
	//		p0Tangent.sub(cp[1], cp[0]);
	//		p0Tangent.scale(3);
	//		outPoints.add(cp[0]);
	//		outDerivs.add(p0Tangent);
	//		
	//		Vector2f[] vLeft = new Vector2f[4];
	//		Vector2f[] vRight = new Vector2f[4];
	//		Vector2f newPoint;
	//		Vector2f deriv;
	//		for (Float u : uValues) {
	//			newPoint = new Vector2f();
	//			deriv = new Vector2f();
	//			newPoint = deCasteljau(cp, u, 0, vLeft, vRight, deriv);
	//			
	//			outPoints.add(newPoint);
	//			outDerivs.add(deriv);
	//			
	//		}
	//	}

	public void tessellate_bezier(Vector2f cp[], float epsilon, ArrayList<Vector2f> outPoints, ArrayList<Vector2f> outDerivs) {

		// add p0 and deriv @ p0 to lists
		Vector2f p0Tangent = new Vector2f();
		p0Tangent.sub(cp[1], cp[0]);
		//p0Tangent.scale(3);
		outPoints.add(cp[0]);
		outDerivs.add(p0Tangent);

		ArrayList<Vector2f> newOutPoints = new ArrayList<Vector2f>();
		ArrayList<Vector2f> newOutDerivs = new ArrayList<Vector2f>();

		tessellateBezierHelper(cp, epsilon, newOutPoints, newOutDerivs, 0);

		outPoints.addAll(newOutPoints);
		outDerivs.addAll(newOutDerivs);
	}

	private void tessellateBezierHelper(Vector2f cp[], float epsilon, ArrayList<Vector2f> outPoints, 
			ArrayList<Vector2f> outDerivs, int numDivs) {

		if (checkAngles(cp, epsilon) || numDivs >= 10)
			return;
		else {
			Vector2f[] vLeft = new Vector2f[4];
			Vector2f[] vRight = new Vector2f[4];
			Vector2f deriv = new Vector2f();
			Vector2f newPoint;
			newPoint = deCasteljau(cp, (float) 0.5, 0, vLeft, vRight, deriv);

			outPoints.add(newPoint);
			outDerivs.add(deriv);

			ArrayList<Vector2f> leftOutPoints = new ArrayList<Vector2f>();
			ArrayList<Vector2f> rightOutPoints = new ArrayList<Vector2f>();
			ArrayList<Vector2f> leftOutDerivs = new ArrayList<Vector2f>();
			ArrayList<Vector2f> rightOutDerivs = new ArrayList<Vector2f>();

			tessellateBezierHelper(vLeft, epsilon, leftOutPoints, leftOutDerivs, numDivs+1);
			tessellateBezierHelper(vRight, epsilon, rightOutPoints, rightOutDerivs, numDivs+1);

			outPoints.addAll(0, leftOutPoints);
			outPoints.addAll(outPoints.size(), rightOutPoints);
			outDerivs.addAll(0, leftOutDerivs);
			outDerivs.addAll(outDerivs.size(), rightOutDerivs);

		}
	}

	private Vector2f deCasteljau(Vector2f[] levelPoints, float u, int level, Vector2f[] vLeft, 
			Vector2f[] vRight, Vector2f deriv) {

		vLeft[level] = levelPoints[0];
		vRight[3-level] = levelPoints[3-level];

		if (level == 2) {
			// calculate new point on the curve, p0
			Vector2f p0 = new Vector2f(levelPoints[0]);
			p0.interpolate(levelPoints[1], u);
			// calculate derivative at p0
			deriv.sub(levelPoints[1], levelPoints[0]);
			//deriv.scale(3);
			vLeft[3] = p0;
			vRight[0] = p0;
			return p0;
		}

		Vector2f[] nextLevelPoints = new Vector2f[levelPoints.length-1];

		for (int i = 0; i < levelPoints.length-1; i++) {
			Vector2f p = new Vector2f(levelPoints[i]);
			p.interpolate(levelPoints[i+1], u);
			nextLevelPoints[i] = p;
		}

		return deCasteljau(nextLevelPoints, u, level+1, vLeft, vRight, deriv);
	}

	private boolean checkAngles(Vector2f[] cp, float epsilon) {
		// calculate the slope of each segment in control polygon
		Vector2f slope01 = new Vector2f(cp[1]);
		Vector2f slope12 = new Vector2f(cp[2]);
		Vector2f slope32 = new Vector2f(cp[2]);
		slope01.sub(cp[0]);
		slope12.sub(cp[1]);
		slope32.sub(cp[3]);

		// use the slopes to get angles
		float angle01 = (float) Math.atan2(slope01.y, slope01.x);
		float angle12 = (float) Math.atan2(slope12.y, slope12.x);
		float angle21 = (float) Math.atan2(-slope12.y, -slope12.x);
		float angle32 = (float) Math.atan2(slope32.y, slope32.x);
		
		angle01 = angle01 >= 0? angle01 : (float)(angle01 + 2*Math.PI);
		angle12 = angle12 >= 0? angle12 : (float)(angle12 + 2*Math.PI);
		angle21 = angle21 >= 0? angle21 : (float)(angle21 + 2*Math.PI);
		angle32 = angle32 >= 0? angle32 : (float)(angle32 + 2*Math.PI);

		// find angles between the segments
		float angleOne = Math.abs(angle01 - angle12);
		float angleTwo = Math.abs(angle32 - angle21);

		return (angleOne < epsilon/2) && (angleTwo < epsilon/2);
	}


	/*
	 * Approximate a single segment of a B-spline with a number of vertices, according to 
	 * an appropriate criterion for how many are needed.  The points on the curve are written 
	 * into the array outPoints, and the corresponding tangent vectors (not normalized) are 
	 * written into outTangents.  The last point is not included.
	 */
	public void tessellate_bspline(Vector2f bspPoints[], float epsilon, ArrayList<Vector2f> outPoints, ArrayList<Vector2f> outDerivs) {
		// Strategy: convert the B-spline segment to a Bezier segment, then approximate that.
		// TODO (Splines P1): Convert the B-spline control points to control points for an equivalent
		// Bezier segment, then tesselate that segment, using the tesselate_bezier function.

		Vector2f[] cp = convertCP_bsp_to_bez(bspPoints);
		tessellate_bezier(cp, epsilon, outPoints, outDerivs);
		
	}

	private Vector2f[] convertCP_bsp_to_bez(Vector2f bspPoints[]) {
		double[] bsp_to_bez = { 1, 4, 1, 0,
								0, 4, 2, 0,
								0, 2, 4, 0,
								0, 1, 4, 1 };
		GMatrix M_bsp_to_bez = new GMatrix(4, 4, bsp_to_bez);

		double[] cp_bsp = { bspPoints[0].x, bspPoints[0].y,
							bspPoints[1].x, bspPoints[1].y,
							bspPoints[2].x, bspPoints[2].y,
							bspPoints[3].x, bspPoints[3].y };
		GMatrix P_bsp = new GMatrix(4, 2, cp_bsp);

		GMatrix P_bez = new GMatrix(4, 2);
		P_bez.mul(M_bsp_to_bez, P_bsp);

		bezPoints[0] = new Vector2f((float) P_bez.getElement(0, 0), (float) P_bez.getElement(0, 1));
		bezPoints[1] = new Vector2f((float) P_bez.getElement(1, 0), (float) P_bez.getElement(1, 1));
		bezPoints[2] = new Vector2f((float) P_bez.getElement(2, 0), (float) P_bez.getElement(2, 1));
		bezPoints[3] = new Vector2f((float) P_bez.getElement(3, 0), (float) P_bez.getElement(3, 1));
		
		return bezPoints;
	}

	@Override
	public void build(GL2 gl, float[] controlPoints, float epsilon) {
		// copy control points into list of vectors
		ArrayList<Vector2f> cp = new ArrayList<Vector2f>();
		int N = controlPoints.length / 2;
		for (int i = 0; i < N; i++)
			cp.add(new Vector2f(controlPoints[2*i], controlPoints[2*i+1]));

		// Use the vertices array list to store your computed vertices
		ArrayList<Vector2f> vertices = new ArrayList<Vector2f>();

		// TODO: (Splines P1)
		// Added array list of tangents to the vertices
		ArrayList<Vector2f> tangents = new ArrayList<Vector2f>();

		//epsilon = 2.0f;
		// For each segment of the spline, call tesselate_bspline to add its points
		if (isClosed) {
			// TODO: Splines Problem 2, Section 4:
			// Compute Bezier control points for a closed curve. Put the computed vertices
			// into the vertices ArrayList declared above.
			
			for (int i = 1; i <= N; i++) {
				bspPoints[0] = cp.get(i-1);
				bspPoints[1] = cp.get(i % N);
				bspPoints[2] = cp.get((i+1) % N);
				bspPoints[3] = cp.get((i+2) % N);
				tessellate_bspline(bspPoints, epsilon, vertices, tangents);
			}
			
		} else {
			// TODO: Splines Problem 1, Section 3.1:
			// Compute Bezier control points for an open curve with boundary conditions.
			// Put the computed vertices into the vertices ArrayList declared above.

			// add dummy nodes
			Vector2f p_neg1 = new Vector2f(cp.get(0));
			Vector2f p_N = new Vector2f(cp.get(cp.size()-1));
			p_neg1.scale(2.0f);
			p_N.scale(2.0f);
			p_neg1.sub(cp.get(1));
			p_N.sub(cp.get(cp.size()-2));
			cp.add(0, p_neg1);
			cp.add(cp.size(), p_N);

			for (int i = 1; i < N; i++) {
				bspPoints[0] = cp.get(i-1);
				bspPoints[1] = cp.get(i);
				bspPoints[2] = cp.get(i+1);
				bspPoints[3] = cp.get(i+2);
				tessellate_bspline(bspPoints, epsilon, vertices, tangents);
			}
		}
		
		// TODO: find a better way to include last endpoint
		// add the last endpoint (control point) in the bezier spline
		Vector2f[] lastPoints = convertCP_bsp_to_bez(bspPoints);
		Vector2f lastDeriv = new Vector2f(lastPoints[3]);
		lastDeriv.sub(lastPoints[2]);
		//lastDeriv.scale(3);  // not supposed to be scaled anyway - need 2nd to last point
		vertices.add(lastPoints[3]);
		tangents.add(lastDeriv);

		ArrayList<Vector2f> normals = tangentsToNormals(tangents);

		float[] flat_vertices = new float[2 * vertices.size()];
		float[] flat_normals = new float[2 * vertices.size()];

		// TODO Splines Problem 1 and 2: Copy the vertices and normals into the flat arrays
		for (int i = 0; i < vertices.size(); i++) {
			flat_vertices[2*i] = vertices.get(i).x/6;
			flat_vertices[2*i+1] = vertices.get(i).y/6;
			flat_normals[2*i] = normals.get(i).x;
			flat_normals[2*i+1] = normals.get(i).y;
		}

		int nvertices = vertices.size();
		length_buffer = new float[nvertices];
		totLength = 0.0f;

		// TODO: PPA2 Problem 3, Section 5.1:
		// Compute the 'normalized' total length values.
		
		length_buffer[0] = 0;
		for (int i = 1; i < nvertices; i++) {
			totLength += distance(vertices.get(i), vertices.get(i-1));
			length_buffer[i] = totLength;
		}
		// divide lengths by total length of spline
		for (int i = 0; i < nvertices; i++) {
			length_buffer[i] = length_buffer[i]/totLength;
		}

		setVertices(gl, flat_vertices);
		setNormals(gl, flat_normals);

		return;
	}

	private ArrayList<Vector2f> tangentsToNormals(ArrayList<Vector2f> tangents) {

		ArrayList<Vector2f> normals = new ArrayList<Vector2f>();

		for (Vector2f t : tangents) {
			Vector2f n = new Vector2f(-t.y, t.x);  // rotate by 90deg CCW
			n.normalize();
			normals.add(n);
		}

		return normals;
	}

	/*
	 * Computes the distance between two points.
	 */
	private static float distance(Vector2f p, Vector2f q) {
		float dx = p.x - q.x, dy = p.y - q.y;
		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	 private List<Object> ctrlsToYamlList()
	    {
	    	List<Object> list = new ArrayList<Object>();
	    	for(Vector2f p : localControlPoints) {
	    		list.add(p.x);
	    		list.add(p.y);
	    	}

	    	return list;
	    }

	    public Object getYamlObjectRepresentation()
	    {
	    	Map<String, Object> result = new HashMap<String, Object>();
			result.put("type", "BSpline");
			result.put("closed", Boolean.toString(isClosed));
			result.put("ctrls", ctrlsToYamlList());
			return result;
	    }

	    @SuppressWarnings("unchecked")
		public void fromYamlObject(Object yamlObject)
		{
	    	// Validate the file.
			if (!(yamlObject instanceof Map))
				throw new RuntimeException("yamlObject not a Map");

			Map<String, Object> yamlMap = (Map<String, Object>)yamlObject;
			Object ctrlObject = yamlMap.get("ctrls");

			if (!yamlMap.containsKey("closed"))
				throw new RuntimeException("Must have a 'closed' entry");

			if (!(ctrlObject instanceof List))
				throw new RuntimeException("ctrlObject not a List");

			List<Double> yamlList = (List<Double>)ctrlObject;

			if (yamlList.size() % 2 > 0)
				throw new RuntimeException("Must have an even number of control point values");

			// Validation complete, attempt to load.
			isClosed = Boolean.valueOf(yamlMap.get("closed").toString());

			localControlPoints.clear();
			for(int i = 0; i < yamlList.size(); i += 2) {
				localControlPoints.add(new Vector2f(
					yamlList.get(i + 0).floatValue(),
					yamlList.get(i + 1).floatValue()));
			}
		}

	    public void save(String filename) throws IOException
		{
			Yaml yaml = new Yaml();
			Object rep = getYamlObjectRepresentation();
			String output = yaml.dump(rep);

			FileWriter fstream = new FileWriter(filename);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(output);
			out.close();
		}

		@SuppressWarnings("unchecked")
		public void load(String filename) throws IOException
		{
			String fileContent = Util.readFileAsString(filename);
			Yaml yaml = new Yaml();
			Object yamlObject = yaml.load(fileContent);

			if (!(yamlObject instanceof Map))
				throw new RuntimeException("yamlObject not a Map");
			Map<String, Object> yamlMap = (Map<String, Object>)yamlObject;

			if (yamlMap.get("type").equals("BSpline"))
				fromYamlObject(yamlObject);
			else
				throw new RuntimeException("invalid BSpline type: " + yamlMap.get("type").toString());
		}
	}
