package cs4620.scene;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.media.opengl.GL2;


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
		}
		else{
			if (keyframes.size() >2){
				int lowerBound = keyframes.floorKey(frame);
				System.out.println(lowerBound+"");
				int upperBound = keyframes.ceilingKey(frame);
				System.out.println(upperBound+ "");
				SceneNode bottom = keyframes.get(lowerBound);
				SceneNode top = keyframes.get(upperBound);
				
				this.setTranslation((bottom.translation.x+top.translation.x)/2, 
									(bottom.translation.y+top.translation.y)/2, 
									(bottom.translation.z + top.translation.z)/2);
				
				this.setScaling((bottom.scaling.x+top.scaling.x)/2,
								(bottom.scaling.y+top.scaling.y)/2,
								(bottom.scaling.z+top.scaling.z)/2);					
			}
			else{
				System.out.println ("FAILURE OF THE 2nd KIND");
			}

		}

	

		//Get list of all the frames
		//Search to see where the current frame is relative to all the frames
		//find closest two frames and linearly interpolate for current frame



	}

	@Override
	public void catmullRomInterpolateTo(int frame) {
		// TODO (Animation P1): Set the state of this node to the specified frame by 
		// interpolating the states of the appropriate keyframes using Catmull-Rom splines.

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
