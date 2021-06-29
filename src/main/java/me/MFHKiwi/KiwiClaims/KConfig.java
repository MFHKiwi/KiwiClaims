package me.MFHKiwi.KiwiClaims;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class KConfig {
	private final Yaml yaml;
	private Map<String, Object> data;
	private final File file;
	private FileReader in;
	private FileWriter out;
	
	public KConfig(String file_name, File data_folder) {
		/*DumperOptions options = new DumperOptions();
		options.setIndent(2);
		options.setPrettyFlow(true);
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);*/
		this.yaml = new Yaml(/*options*/);
		this.file = new File(data_folder + File.separator + file_name);
	}
	
	@SuppressWarnings("unchecked")
	public boolean load() {
		try {
			this.in = new FileReader(file);
		} catch (FileNotFoundException e) {
			return false;
		}
		this.data = (Map<String, Object>) this.yaml.load(this.in);
		return true;
	}
	
	public void save() {
		try {
			this.out = new FileWriter(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.yaml.dump(this.data, this.out);
	}
	
	public void set(String name, Object object) {
		this.data.put(name, object);
	}
	
	public void set(Map<String, Object> data) {
		this.data = data;
	}
	
	public Object get(String name) {
		return this.data.get(name);
	}
}
