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
		float w1 = i1.x;
		float x1 = i1.y;
		float y1 = i1.z;
		float z1 = i1.w;
		
		float w2 = i2.x;
		float x2 = i2.y;
		float y2 = i2.z;
		float z2 = i2.w;
		
		float dotProd = w1*w2+x1*x2+y1*y2+z1*z2;
		System.out.println (""+dotProd);
		float psi = (float) Math.acos(dotProd);
		
		if (!Float.isNaN(psi) || Math.abs(psi)>0.001)
			if (dotProd>=0){
				psi = (float) 0.001;
				}
				else{
					psi = (float)-0.001;
				}
		else{
		}
		
	//	if ( Float.isNaN(dotProd) && Math.abs(dotProd)>=0.001){
		//	psi = (float) Math.acos(dotProd);
	//	}
	/*	else{
			if (dotProd>=0){
			psi = (float) 0.001;
			}
			else{
				psi = (float)-0.001;
			}
		}*/
		System.out.println ("psi: "+Math.toDegrees(psi));
		
		i1.scale((float) (Math.sin((1-t)*psi)/Math.sin(psi)));
		i2.scale((float) (Math.sin(t*psi)/Math.sin(psi)));
		
		System.out.println(Math.sin(1-t)*psi);
		System.out.println(Math.sin(t*psi));
		
		Quat4f lingo = i1;
		lingo.add(i2);
		return lingo;

	}

	public static Vector3f getEulerAnglesFromQuaternion(Quat4f quat)
	{
		// TODO (Animation P1): Convert the given quaternion into a vector of 
		// three Euler angles that encode the same rotation.

		float w = quat.x;
		float x = quat.y;
		float y = quat.z;
		float z = quat.w;

		float a;
		float b;
		float c;

		float test = 2*(w*y-z*x);
		System.out.println("WX "+w*y);
		System.out.println("ZX " +z*x);
		System.out.println("TEST "+ test);
		if (Math.abs(test)>0.9999){
		//if (Float.isNaN(test) ){
			if (test>0){
				a =(float) (2*Math.atan2(x,w));
				b = (float) (Math.PI/2);
				c = 0;
				System.out.println("HEY I'M HERE");
			}
			else{
				a =(float) (-2*Math.atan2(x,w));
				b = (float) (-Math.PI/2);
				c = 0;
			}
		}
		else{
			a = (float) (Math.atan2(2*(w*x+y*z),1-2*(Math.pow(x,2)+Math.pow(y,2))));
			b = (float)(Math.asin(2*(w*y-z*x)));
			c = (float) (Math.atan2(2*(w*z+x*y),1-2*(Math.pow(y,2)+Math.pow(z,2))));
		}

		System.out.println("a: " + a);
		System.out.println("b: "+b);
		System.out.println("c: " +c);
		
		float final_a = (float)Math.toDegrees(a);
		float final_b = (float)Math.toDegrees(b);
		float final_c = (float)Math.toDegrees(c);
		
		Vector3f mingo = new Vector3f(final_a,final_b,final_c);
		return mingo;

	}

	public static Quat4f getQuaternionFromEulerAngles(Vector3f eulerAngles)
	{
		// TODO (Animation P1): Convert the given Euler angles into a quaternion
		// that encodes the same rotation.
		//qx = hcos(a/2), sin(a/2)  (1, 0, 0)i
		//qy = hcos(b/2), sin(b/2)  (0, 1, 0)i
		//qz = hcos(c/2), sin(c/2)  (0, 0, 1)i

		Vector3f x = new Vector3f(1,0,0);
		Vector3f y = new Vector3f(0,1,0);
		Vector3f z = new Vector3f(0,0,1);

		float a = (float) Math.toRadians(eulerAngles.x);
		float b = (float) Math.toRadians(eulerAngles.y);
		float c = (float) Math.toRadians(eulerAngles.z);

		x.scale((float) Math.sin(a/2));
		y.scale((float) Math.sin(b/2));
		z.scale((float) Math.sin(c/2));

		Quat4f qx = new Quat4f((float)Math.cos(a/2),x.x,x.y,x.z);
		Quat4f qy = new Quat4f((float)Math.cos(b/2),y.x,y.y,y.z);
		Quat4f qz = new Quat4f((float)Math.cos(c/2),z.x,z.y,z.z);

		Quat4f bingo = qy;

		bingo.mul(qx);
		qz.mul(bingo);

		return qz;
	}
}
