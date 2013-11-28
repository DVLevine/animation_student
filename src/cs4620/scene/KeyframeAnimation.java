package cs4620.scene;

import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

public class KeyframeAnimation {
	public static void linearInterpolateTransformation(SceneNode sNode, SceneNode eNode, float t, SceneNode iNode)
	{
		// TODO (Animation P1): Implement if you find useful -- set the state of SceneNode iNode
		// (which can be a superclass of SceneNode, such as SceneNodeKeyframeable) to be
		// the linear interpolation of the states of nodes sNode and eNode.
		
	}
	
	public static void catmullRomInterpolateTransformation(SceneNode p0, SceneNode p1,
			SceneNode p2, SceneNode p3, float t, SceneNode iNode)
	{
		// TODO (Animation P1): Implement if you find useful -- set the state of SceneNode iNode
		// (which can be a superclass of SceneNode, such as SceneNodeKeyframeable) to be the
		// result of evaluating the Catmull-Rom spline whose "control points" are the states
		// of the four SceneNode objects.
		
	}
	
	public static void linearInterpolate4Float(float [] sFloat, float [] eFloat, float t, float [] iFloat)
	{
		// TODO (Animation P1): Implement if you find useful -- interpolate linearly between two size-four
		// arrays and write result to iFloat.
		
	}
	
	public static void catmullRomInterpolate4Float(float [] p0, float [] p1, float [] p2, float [] p3,
			float t, float [] iNode)
	{
		// TODO (Animation P1): Implement if you find useful -- evaluate a Catmull-Rom spline on four
		// four-element-array "control points" and write result to iNode.
		
	}
	
	public static void catmullRomRotationInterpolation(Vector3f p0, Vector3f p1,
	        Vector3f p2, Vector3f p3, float t, Vector3f iNode)
	{
		// TODO (Animation P1): Implement if you find useful -- evaluate a Catmull-Rom spline
		// using quaternions with the given four control points, and write result to iNode.
		
	}
	
	public static void catmullRomInterpolation(Vector3f p0, Vector3f p1,
			Vector3f p2, Vector3f p3, float t, Vector3f iNode)
	{
		// TODO (Animation P1): Implement if you find useful -- evaluate a Catmull-Rom spline
		// with the given four control points, and write result to iNode.
				
	}
	
	public static Quat4f slerp(Quat4f i1, Quat4f i2, float t)
	{
		// TODO (Animation P1): Implement slerp.
		
	}
	
	public static Vector3f getEulerAnglesFromQuaternion(Quat4f quat)
	{
		// TODO (Animation P1): Convert the given quaternion into a vector of 
		// three Euler angles that encode the same rotation.
		
	}
	
	public static Quat4f getQuaternionFromEulerAngles(Vector3f eulerAngles)
	{
		// TODO (Animation P1): Convert the given Euler angles into a quaternion
		// that encodes the same rotation.
		
	}
}
