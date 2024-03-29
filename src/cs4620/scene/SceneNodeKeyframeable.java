package cs4620.scene;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.media.opengl.GL2;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;


public class SceneNodeKeyframeable extends SceneNode
implements Keyframeable {
	private static final long serialVersionUID = 4345300320997053825L;

	protected TreeMap<Integer, SceneNode> keyframes = new TreeMap<Integer, SceneNode>();

	public SceneNodeKeyframeable()
	{
		super();
		keyframes.put(0, new SceneNode());

	}

	public SceneNodeKeyframeable(String name)
	{
		super(name);
		resetTransformation();
		keyframes.put(0, new SceneNode(name));
	}

	public SceneNodeKeyframeable(String name, int [] frames)
	{
		super(name);
		for(int f : frames)
		{
			keyframes.put(f, new SceneNode(name));
		}
	}

	public void setToTransformation(SceneNode node)
	{
		rotation.set(node.rotation);
		scaling.set(node.scaling);
		translation.set(node.translation);
	}

	@Override
	public int[] getFrameNumbers() {
		Integer [] integerArray = keyframes.keySet().toArray(new Integer[0]);
		int [] outArray = new int[integerArray.length];
		for(int i = 0; i < outArray.length; i++)
			outArray[i] = integerArray[i];
		return outArray;
	}

	@Override
	public void addAsKeyframe(int frame) {
		SceneNode keyframeNode = new SceneNode(getName());
		keyframeNode.rotation.set(rotation);
		keyframeNode.scaling.set(scaling);
		keyframeNode.translation.set(translation);

		/*
		System.out.println("x trans "+keyframeNode.translation.x);
		System.out.println("y trans "+keyframeNode.translation.y);
		System.out.println("z trans "+keyframeNode.translation.z);
		System.out.println("------------------------------------");
		 */
		keyframes.put(frame, keyframeNode);
	}

	@Override
	public void applyToAllKeyframes()
	{
		int [] keyframeNumbers = getFrameNumbers();

		for(int f : keyframeNumbers)
		{
			addAsKeyframe(f);
		}
	}

	@Override
	public void deleteKeyframe(int frame) {
		keyframes.remove(new Integer(frame));
	}

	
	
	@Override
	public void linearInterpolateTo(int frame) {
		// TODO (Animation P1): Set the state of this node to the specified frame by
		// linearly interpolating the states of the appropriate keyframes.

		//TreeMap<Integer, SceneNode> keyframes = new TreeMap<Integer, SceneNode>();

		if (keyframes.containsKey(frame)){
			SceneNode bing = keyframes.get(frame);
			this.setTranslation(bing.translation.x, bing.translation.y, bing.translation.z);
			this.setScaling(bing.scaling.x, bing.scaling.y, bing.scaling.z);
			return;
		}
		else{
			if (keyframes.ceilingKey(frame)==null && keyframes.floorKey(frame)==null){
				return;
			}
			else if (keyframes.ceilingKey(frame)==null){
				return;
			}
			else if (keyframes.floorKey(frame) == null){
				return;
			}
			else {	
				int upperBound = keyframes.ceilingKey(frame);
				int lowerBound = keyframes.floorKey(frame);

				SceneNode bottom = keyframes.get(lowerBound);
				SceneNode top = keyframes.get(upperBound);
				
				Vector3f bTrans = bottom.translation;
				Vector3f tTrans = top.translation;
				
				float Tweight = (float)(frame-lowerBound)/(float)(upperBound-lowerBound);
				float Bweight = (float)(upperBound-frame)/(float)(upperBound-lowerBound);
				

				this.setTranslation(	
						(Bweight*bottom.translation.x + Tweight*top.translation.x),
						(Bweight*bottom.translation.y + Tweight*top.translation.y), 
						(Bweight*bottom.translation.z + Tweight*top.translation.z));

				/*System.out.println("x trans "+this.translation.x);
				System.out.println("y trans "+this.translation.y);
				System.out.println("z trans "+this.translation.z);
				System.out.println("------------------------------------");*/

				this.setScaling(
						(Bweight*bottom.scaling.x+Tweight*top.scaling.x),
						(Bweight*bottom.scaling.y+Tweight*top.scaling.y),
						(Bweight*bottom.scaling.z+Tweight*top.scaling.z));		
				
				
				Quat4f botQuat = KeyframeAnimation.getQuaternionFromEulerAngles(bottom.rotation);
				Quat4f topQuat = KeyframeAnimation.getQuaternionFromEulerAngles(top.rotation);
				Quat4f resultQ = KeyframeAnimation.slerp(botQuat, topQuat, Tweight);
				Vector3f resultV = KeyframeAnimation.getEulerAnglesFromQuaternion(resultQ);
				
				/*this.setRotation(resultV.x,resultV.y,resultV.z);
				System.out.print("Rotation x: " + resultV.x);
				System.out.print(" Rotationy: " + resultV.y);
				System.out.println(" Rotationz: " + resultV.z);*/
				
				
						
				
			}	
		}

	}



	//Get list of all the frames
	//Search to see where the current frame is relative to all the frames
	//find closest two frames and linearly interpolate for current frame





	@Override
	public void catmullRomInterpolateTo(int frame) {
		// TODO (Animation P1): Set the state of this node to the specified frame by 
		// interpolating the states of the appropriate keyframes using Catmull-Rom splines.
		if (keyframes.containsKey(frame)){
			SceneNode bing = keyframes.get(frame);
			this.setTranslation(bing.translation.x, bing.translation.y, bing.translation.z);
			this.setScaling(bing.scaling.x, bing.scaling.y, bing.scaling.z);
			return;
		}
		else{
			if (keyframes.ceilingKey(frame)==null && keyframes.floorKey(frame)==null){
				return;
			}
			else if (keyframes.ceilingKey(frame)==null){
				return;
			}
			else if (keyframes.floorKey(frame) == null){
				return;
			}
			else {	
				int upperBound = keyframes.ceilingKey(frame);
				int lowerBound = keyframes.floorKey(frame);
				int topBound;
				int botBound;
				
				if (keyframes.ceilingKey(upperBound) == null){
					topBound = upperBound;
				}
				else{
					topBound = keyframes.ceilingKey(upperBound);
				}
				
				if (keyframes.ceilingKey(lowerBound) == null){
					botBound = lowerBound;
				}
				else{
					botBound = keyframes.ceilingKey(lowerBound);
				}
				
				SceneNode bottom = keyframes.get(lowerBound);
				SceneNode top = keyframes.get(upperBound);

				float Tweight = (float)(frame-lowerBound)/(float)(upperBound-lowerBound);
				float Bweight = (float)(upperBound-frame)/(float)(upperBound-lowerBound);

				Matrix4f splineMat = new Matrix4f(0, 2, 0, 0,
												 -1, 0, 1, 0,
												  2,-5, 4,-1,
												 -1, 3,-3, 1);
				
				Vector4f pointVec = new Vector4f(botBound,lowerBound,upperBound,topBound);
				Vector4f timeVecTop = new Vector4f((float)0.5,(float)0.5*Tweight, (float)(0.5*Math.pow(Tweight, 2)),(float)(0.5*Math.pow(Tweight, 3)));
				Vector4f timeVecBot = new Vector4f((float)0.5,(float)0.5*Bweight, (float)(0.5*Math.pow(Bweight, 2)),(float)(0.5*Math.pow(Bweight, 3)));
					//	,(float)0.5);
				/*		
				System.out.print("w: "+timeVecTop.w);
				System.out.print(" x: "+timeVecTop.x);
				System.out.print(" y: "+timeVecTop.y);
				System.out.println(" z: " + timeVecTop.z);
				*/
				Vector4f rhs = new Vector4f();
				
				splineMat.transform(pointVec, rhs);
				Tweight = ((timeVecTop.dot(rhs))/(upperBound-lowerBound)) % 1;
				Bweight = ((timeVecBot.dot(rhs))/(upperBound-lowerBound)) % 1;
				
			/*	System.out.print("w: "+rhs.w);
				System.out.print(" x: "+rhs.x);
				System.out.print(" y: "+rhs.y);
				System.out.println(" z: " + rhs.z);
				System.out.println("result: "+Tweight);*/
				
			//	System.out.println(""+Tweight);
				//System.out.println(""+Bweight);
				//System.out.println("Bweight "+Bweight);

				this.setTranslation(
						(Bweight*bottom.translation.x + 
								Tweight*top.translation.x),
						(Bweight*bottom.translation.y + 
								Tweight*top.translation.y), 
						(Bweight*bottom.translation.z + 
								Tweight*top.translation.z));

				/*System.out.println("x trans "+this.translation.x);
				System.out.println("y trans "+this.translation.y);
				System.out.println("z trans "+this.translation.z);
				System.out.println("------------------------------------");*/

				this.setScaling(
						(Bweight*bottom.scaling.x+Tweight*top.scaling.x),
						(Bweight*bottom.scaling.y+Tweight*top.scaling.y),
						(Bweight*bottom.scaling.z+Tweight*top.scaling.z));		
			}	
		}
		
		
	}

	@Override
	public void setName(String name) {
		this.name = name;
		if(keyframes != null)
		{
			for(Entry<Integer, SceneNode> entry : keyframes.entrySet())
			{
				entry.getValue().setName(name);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getYamlObjectRepresentation()
	{
		Map<String, Object> result = (Map<String, Object>)super.getYamlObjectRepresentation();
		result.put("type", "SceneNodeKeyframeable");

		Map<Object, Object> framesMap = new HashMap<Object, Object>();
		for (Entry<Integer, SceneNode> entry: keyframes.entrySet())
		{
			framesMap.put(entry.getKey(), entry.getValue().getYamlObjectRepresentation());
		}
		result.put("frames", framesMap);

		return result;
	}

	public void extractFramesFromYamlObject(GL2 gl, Object yamlObject)
	{
		if (!(yamlObject instanceof Map))
			throw new RuntimeException("yamlObject not a Map");
		Map<?,?> yamlMap = (Map<?,?>)yamlObject;

		Map<Object, Object> framesMap = (Map) yamlMap.get("frames");

		for (Entry<Object, Object> entry: framesMap.entrySet())
		{
			int frameIndex = Integer.parseInt(entry.getKey().toString());
			SceneNode node = (SceneNode) SceneNode.fromYamlObject(gl, entry.getValue());
			keyframes.put(frameIndex, node);
		}
	}

	public static SceneNode fromYamlObject(GL2 gl, Object yamlObject)
	{
		if (!(yamlObject instanceof Map))
			throw new RuntimeException("yamlObject not a Map");
		Map<?,?> yamlMap = (Map<?,?>)yamlObject;

		SceneNodeKeyframeable result = new SceneNodeKeyframeable((String)yamlMap.get("name"));
		result.extractFramesFromYamlObject(gl, yamlObject);
		result.extractTransformationFromYamlObject(yamlObject);
		result.addChildrenFromYamlObject(gl, yamlObject);

		return result;
	}

}
