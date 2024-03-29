package cs4620.manip;

import javax.media.opengl.GL2;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import cs4620.scene.SceneNode;
import cs4620.scene.SceneProgram;
import cs4620.shape.Cube;
import cs4620.shape.TriangleMesh;
import cs4620.framework.Camera;
import cs4620.framework.Transforms;
import cs4620.manip.ManipUtils;
import cs4620.material.Material;
import cs4620.material.PhongMaterial;

public class TranslateManip extends Manip
{
	
	Cube cube = null;
	ArrowHeadMesh arrow = null;
	PhongMaterial xMaterial;
	PhongMaterial yMaterial;
	PhongMaterial zMaterial;
	PhongMaterial centerMaterial;
	boolean resourcesInitialized = false;
	
	private void initResourcesGL(GL2 gl)
	{
		if (!resourcesInitialized)
		{
			cube = new Cube(gl);
			cube.buildMesh(gl, 1.0f);
			arrow = new ArrowHeadMesh(gl);
			arrow.buildMesh(gl, 1.0f);
			
			xMaterial = new PhongMaterial();
			xMaterial.setAmbient(0.0f, 0.0f, 0.0f);
			xMaterial.setDiffuse(0.8f, 0.0f, 0.0f);
			xMaterial.setSpecular(0.0f, 0.0f, 0.0f);
			
			yMaterial = new PhongMaterial();
			yMaterial.setAmbient(0.0f, 0.0f, 0.0f);
			yMaterial.setDiffuse(0.0f, 0.8f, 0.0f);
			yMaterial.setSpecular(0.0f, 0.0f, 0.0f);
			
			zMaterial = new PhongMaterial();
			zMaterial.setAmbient(0.0f, 0.0f, 0.0f);
			zMaterial.setDiffuse(0.0f, 0.0f, 0.8f);
			zMaterial.setSpecular(0.0f, 0.0f, 0.0f);
			
			centerMaterial = new PhongMaterial();
			centerMaterial.setAmbient(0.0f, 0.0f, 0.0f);
			centerMaterial.setDiffuse(0.8f, 0.8f, 0.0f);
			centerMaterial.setSpecular(0.0f, 0.0f, 0.0f);
			resourcesInitialized = true;
		}
	}
	

	@Override
	public void dragged(Vector2f mousePosition, Vector2f mouseDelta)
	{
		// TODO (Manipulators P1): Implement this manipulator.
	    //Obtained World Vectors
		Vector3f clickPoint = new Vector3f();
		Vector3f clickDir = new Vector3f();

		Vector2f startPos = new Vector2f(mousePosition.x - mouseDelta.x, mousePosition.y - mouseDelta.y);
		camera.getRayNDC(startPos, clickPoint, clickDir);

		Vector3f endPoint = new Vector3f();
		Vector3f endDir = new Vector3f();

		camera.getRayNDC(mousePosition, endPoint, endDir);

		//Get Axis of Consideration
		Matrix4f manipFrameToWorld = sceneNode.getSceneNodeParent().toWorld();
		Vector3f axisOrigin = new Vector3f(e0);
		Vector3f axisDir;

		manipFrameToWorld.transform(axisOrigin);

		float t0, t1;
		Vector3f translate = sceneNode.translation;
		if (axisMode == PICK_CENTER){
			t0 = ManipUtils.intersectRayPlane(clickPoint,clickDir, axisOrigin, camera.getViewDir());
			t1 = ManipUtils.intersectRayPlane(endPoint, endDir, axisOrigin, camera.getViewDir());

			clickDir.scale(t0);
			clickPoint.add(clickDir); 

			endDir.scale(t1);
			endPoint.add(endDir);
			endPoint.sub(clickPoint);

			Matrix4f invertWorld = new Matrix4f();
			invertWorld.invert(manipFrameToWorld);

			invertWorld.transform(endPoint);

			sceneNode.setTranslation(translate.x + endPoint.x, translate.y + endPoint.y, translate.z + endPoint.z);	
		}

		else if (axisMode == PICK_X) {
			axisDir = new Vector3f(eX);
			manipFrameToWorld.transform(axisDir);

			t0 = ManipUtils.timeClosestToRay(axisOrigin, axisDir, clickPoint, clickDir);
			t1 = ManipUtils.timeClosestToRay(axisOrigin, axisDir, endPoint, endDir);

			sceneNode.setTranslation(translate.x + (t1-t0), translate.y, translate.z);
		}
		else if (axisMode == PICK_Y){
			axisDir = new Vector3f(eY);
			manipFrameToWorld.transform(axisDir);

			t0 = ManipUtils.timeClosestToRay(axisOrigin, axisDir, clickPoint, clickDir);
			t1 = ManipUtils.timeClosestToRay(axisOrigin, axisDir, endPoint, endDir);

			sceneNode.setTranslation(translate.x, translate.y + (t1-t0), translate.z);
		}
		else if (axisMode == PICK_Z){
			axisDir = new Vector3f(eZ);
			manipFrameToWorld.transform(axisDir);

			t0 = ManipUtils.timeClosestToRay(axisOrigin, axisDir, clickPoint, clickDir);
			t1 = ManipUtils.timeClosestToRay(axisOrigin, axisDir, endPoint, endDir);

			sceneNode.setTranslation(translate.x, translate.y, translate.z + (t1-t0));
		}	
	}

	@Override
	public void glRender(GL2 gl, SceneProgram program, Matrix4f modelView, double scale)
	{
		initResourcesGL(gl);
		
		SceneNode parent = sceneNode.getSceneNodeParent();
		Matrix4f parentModelView;
		if (parent != null)
		{
			parentModelView = parent.toEye(modelView);
		}
		else
		{
			parentModelView = new Matrix4f(modelView);
		}
		
		// get eye-space vectors for x,y,z axes of parent
		Vector3f eyeX = new Vector3f();
		Vector3f eyeY = new Vector3f();
		Vector3f eyeZ = new Vector3f();
		Transforms.toColumns3DH(parentModelView, eyeX, eyeY, eyeZ, null);
		
		// get eye-space position of translated origin of sceneNode
		Vector3f eyeOrigin = new Vector3f(sceneNode.translation);
		ManipUtils.transformPosition(parentModelView, eyeOrigin);
		
		// translation to eye-space translated origin of sceneNode
		Matrix4f translateOrigin = Transforms.translate3DH(eyeOrigin);
		Matrix4f scaleMatrix = Transforms.scale3DH((float) scale);
		
		Matrix4f nextModelView = new Matrix4f();
		
		// x axis
		nextModelView.set(translateOrigin);
		nextModelView.mul(ManipUtils.rotateZTo(eyeX));
		nextModelView.mul(scaleMatrix);
		setIdIfPicking(gl, program, Manip.PICK_X);
		glRenderArrow(gl, program, nextModelView, 0);
		
		// y axis
		nextModelView.set(translateOrigin);
		nextModelView.mul(ManipUtils.rotateZTo(eyeY));
		nextModelView.mul(scaleMatrix);
		setIdIfPicking(gl, program, Manip.PICK_Y);
		glRenderArrow(gl, program, nextModelView, 1);
		
		// z axis
		nextModelView.set(translateOrigin);
		nextModelView.mul(ManipUtils.rotateZTo(eyeZ));
		nextModelView.mul(scaleMatrix);
		setIdIfPicking(gl, program, Manip.PICK_Z);
		glRenderArrow(gl, program, nextModelView, 2);
		
		float boxRadius = 0.1f;
		// center cube
		nextModelView.set(translateOrigin);
		nextModelView.mul(scaleMatrix);
		nextModelView.mul(Transforms.scale3DH(boxRadius));
		program.setModelView(gl, nextModelView);
		setIdIfPicking(gl, program, Manip.PICK_CENTER);
		gl.glDisable(GL2.GL_DEPTH_TEST);
		centerMaterial.drawUsingProgram(gl, program, cube, false);  // cube should draw over other parts of manipulator
		gl.glEnable(GL2.GL_DEPTH_TEST);
	}

	public void glRenderArrow(GL2 gl, SceneProgram program, Matrix4f modelView, int axis) {
		Material axisMaterial = zMaterial;
		if (axis == 0) // x
		{
			axisMaterial = xMaterial;
		}
		else if (axis == 1) // y
		{
			axisMaterial = yMaterial;
		}
		
		float radiusTail = 0.075f;
		float radiusHead = 0.15f;
		float lengthTail = 1.7f;
		float lengthHead = 0.3f;
		
		// tail
		Matrix4f nextModelView = new Matrix4f(modelView);
		nextModelView.mul(Transforms.scale3DH(radiusTail, radiusTail, -lengthTail / 2.0f));
		nextModelView.mul(Transforms.translate3DH(0.0f, 0.0f, -1.0f));
		program.setModelView(gl, nextModelView);
		axisMaterial.drawUsingProgram(gl, program, arrow, false);
		
		// tail
		nextModelView.set(modelView);
		nextModelView.mul(Transforms.translate3DH(0.0f, 0.0f, lengthTail));
		nextModelView.mul(Transforms.scale3DH(radiusHead, radiusHead, lengthHead / 2.0f));
		nextModelView.mul(Transforms.translate3DH(0.0f, 0.0f, 1.0f));
		program.setModelView(gl, nextModelView);
		axisMaterial.drawUsingProgram(gl, program, arrow, false);
		
	}
}

class ArrowHeadMesh extends TriangleMesh
{
	
	public static final int CIRCLE_DIVS = 32;

	public ArrowHeadMesh(GL2 gl) {
		super(gl);
	}
	
	float cos(float ang)
	{
		return (float) Math.cos(ang);
	}
	
	float sin(float ang)
	{
		return (float) Math.sin(ang);
	}

	@Override
	public void buildMesh(GL2 gl, float tolerance) {
		// build the arrowhead!
		float [] vertices = new float [3 * CIRCLE_DIVS * 2];
		float [] normals = new float [3 * CIRCLE_DIVS * 2];
		
		int [] triangles = new int [3 * CIRCLE_DIVS];
		
		float pi = (float) Math.PI;
		float xyCoeff = 2.0f / (float) Math.sqrt(5.0f);
		float zCoeff = 1.0f / (float) Math.sqrt(5.0f);
		// form vertices and normals
		for(int i = 0; i < CIRCLE_DIVS; i++)
		{
			float ang = i * 2.0f * pi / CIRCLE_DIVS;
			float angBetween = (2 * i + 1) * 2.0f * pi / (2 * CIRCLE_DIVS);
			
			vertices[6*i + 0] = cos(ang);
			vertices[6*i + 1] = sin(ang);
			vertices[6*i + 2] = -1.0f;
			normals[6*i + 0] = cos(ang) * xyCoeff;
			normals[6*i + 1] = sin(ang) * xyCoeff;
			normals[6*i + 2] = zCoeff;
			
			vertices[6*i + 3] = 0.0f;
			vertices[6*i + 4] = 0.0f;
			vertices[6*i + 5] = 1.0f;
			normals[6*i + 3] = cos(angBetween) * xyCoeff;
			normals[6*i + 4] = sin(angBetween) * xyCoeff;
			normals[6*i + 5] = zCoeff;
		}
		
		// assemble triangles
		for(int i = 0; i < CIRCLE_DIVS; i++)
		{
			triangles[3 * i + 0] = 2*i;
			triangles[3 * i + 1] = (2*i + 2) % (2 * CIRCLE_DIVS);
			triangles[3 * i + 2] = 2*i+1;
		}
		
		this.setVertices(gl, vertices);
		this.setNormals(gl, normals);
		this.setTriangleIndices(gl, triangles);
		// we won't need wireframe here
		this.setWireframeIndices(gl, new int [0]);
	}

	@Override
	public Object getYamlObjectRepresentation() {
		return null;
	}
	
}
