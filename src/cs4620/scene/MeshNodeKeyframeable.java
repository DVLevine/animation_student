package cs4620.scene;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.media.opengl.GL2;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import cs4620.material.Material;
import cs4620.shape.Mesh;

public class MeshNodeKeyframeable extends MeshNode implements Keyframeable {
	private static final long serialVersionUID = -2756185791348683068L;

	protected TreeMap<Integer, MeshNode> keyframes = new TreeMap<Integer, MeshNode>();

	public MeshNodeKeyframeable()
	{
		super();
		keyframes.put(0, new MeshNode());
	}

	public MeshNodeKeyframeable(GL2 gl, String name)
	{
		super(gl, name);
		keyframes.put(0, new MeshNode(gl, name));
	}

	public MeshNodeKeyframeable(String name, Mesh mesh)
	{
		super(name, mesh);
		keyframes.put(0, new MeshNode(name, mesh));
	}

	public MeshNodeKeyframeable(String name, Mesh mesh, Material material)
	{
		super(name, mesh, material);
		keyframes.put(0, new MeshNode(name, mesh, material));
	}

	public MeshNodeKeyframeable(String name, Mesh mesh, int [] frames)
	{
		super(name, mesh);
		for(int f : frames)
		{
			keyframes.put(f, new MeshNode(name, mesh));
		}
	}

	public MeshNodeKeyframeable(GL2 gl, String name, int[] frames)
	{
		super(gl, name);
		for(int f : frames)
		{
			keyframes.put(f, new MeshNode(gl, name));
		}
	}

	public MeshNodeKeyframeable(String name, Mesh mesh, Material mat,
			int[] frames) {
		super(name, mesh, mat);
		for(int f : frames)
		{
			keyframes.put(f, new MeshNode(name, mesh, mat));
		}
	}

	public void setToMeshNode(MeshNode node)
	{
		rotation.set(node.rotation);
		scaling.set(node.scaling);
		translation.set(node.translation);
	}

	@Override
	public int[] getFrameNumbers()
	{
		Integer [] integerArray = keyframes.keySet().toArray(new Integer[0]);
		int [] outArray = new int[integerArray.length];
		for(int i = 0; i < outArray.length; i++)
			outArray[i] = integerArray[i];
		return outArray;
	}

	@Override
	public void addAsKeyframe(int frame)
	{
		MeshNode keyframeNode = new MeshNode(getName(), getMesh(), getMaterial());
		keyframeNode.rotation.set(rotation);
		keyframeNode.scaling.set(scaling);
		keyframeNode.translation.set(translation);

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
	public void deleteKeyframe(int frame)
	{
		keyframes.remove(new Integer(frame));

	}

	@Override
	public void linearInterpolateTo(int frame)
	{
		// TODO (Animation P1): Set the state of this node to the specified frame by
		// linearly interpolating the states of the appropriate keyframes.

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

				float Tweight = (float)(frame-lowerBound)/(float)(upperBound-lowerBound);
				float Bweight = (float)(upperBound-frame)/(float)(upperBound-lowerBound);

				this.setTranslation(	
						(Bweight*bottom.translation.x + Tweight*top.translation.x),
						(Bweight*bottom.translation.y + Tweight*top.translation.y), 
						(Bweight*bottom.translation.z + Tweight*top.translation.z));

				this.setScaling(
						(Bweight*bottom.scaling.x+Tweight*top.scaling.x),
						(Bweight*bottom.scaling.y+Tweight*top.scaling.y),
						(Bweight*bottom.scaling.z+Tweight*top.scaling.z));
				
				Quat4f botQuat = KeyframeAnimation.getQuaternionFromEulerAngles(bottom.rotation);
				Quat4f topQuat = KeyframeAnimation.getQuaternionFromEulerAngles(top.rotation);
				Quat4f resultQ = KeyframeAnimation.slerp(botQuat, topQuat, Tweight);
				Vector3f resultV = KeyframeAnimation.getEulerAnglesFromQuaternion(resultQ);
				
				this.setRotation(resultV.x,resultV.y,resultV.z);
				System.out.print("Rotation x: " + resultV.x);
				System.out.print(" Rotation.y: " + resultV.y);
				System.out.println(" Rotation.z: " + resultV.z);


			}	
		}

	}

	@Override
	public void catmullRomInterpolateTo(int frame)
	{
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

			/*	SceneNode ancient = keyframes.get(botBound);
				SceneNode old = keyframes.get(lowerBound);
				SceneNode present = keyframes.get(upperBound);
				SceneNode future = keyframes.get(topBound);*/

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

				Vector4f rhs = new Vector4f();

				splineMat.transform(pointVec, rhs);
				Tweight = ((timeVecTop.dot(rhs))/(upperBound-lowerBound)) % 1;
				Bweight = ((timeVecBot.dot(rhs))/(upperBound-lowerBound)) % 1;
				
				
				this.setTranslation(
						(Bweight*bottom.translation.x + Tweight*top.translation.x),
						(Bweight*bottom.translation.y + Tweight*top.translation.y), 
						(Bweight*bottom.translation.z + Tweight*top.translation.z));

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
			for(Entry<Integer, MeshNode> entry : keyframes.entrySet())
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
		result.put("type", "MeshNodeKeyframeable");

		Map<Object, Object> framesMap = new HashMap<Object, Object>();
		for (Entry<Integer, MeshNode> entry: keyframes.entrySet())
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
			MeshNode node = (MeshNode) MeshNode.fromYamlObject(gl, entry.getValue());
			keyframes.put(frameIndex, node);
		}
	}

	public static SceneNode fromYamlObject(GL2 gl, Object yamlObject)
	{
		if (!(yamlObject instanceof Map))
			throw new RuntimeException("yamlObject not a Map");
		Map<?,?> yamlMap = (Map<?,?>)yamlObject;

		MeshNodeKeyframeable result = new MeshNodeKeyframeable(gl, (String)yamlMap.get("name"));
		result.extractTransformationFromYamlObject(yamlObject);
		result.addChildrenFromYamlObject(gl, yamlObject);
		result.extractMeshFromYamlObject(gl, yamlObject);
		result.extractMaterialFromYamlObject(yamlObject);
		result.extractFramesFromYamlObject(gl, yamlObject);

		return result;
	}

}
