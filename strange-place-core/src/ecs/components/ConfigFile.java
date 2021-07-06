package ecs.components;

import com.artemis.Component;

import configs.CharConfig;
import configs.CropConfig;

public class ConfigFile extends Component
{
	//private static final String TAG = ConfigFile.class.getSimpleName();
	
	public String fileName = "";
	public CharConfig charConfig;
	public CropConfig cropConfig;

	public ConfigFile()
	{
		//this.charConfig = new CharConfig();
		//this.cropConfig = new CropConfigV2();
	}
}