/**
 * 
 */
package net.jueb.fuckGame.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wang
 * 
 */
public class JProperties extends Properties {
	private static final long serialVersionUID = -4599023842346938325L;
	protected Logger _log = LoggerFactory.getLogger(this.getClass());
	private boolean _warn = true;

	public JProperties() {
	}

	public JProperties setLog(boolean warn) {
		_warn = warn;

		return this;
	}

	// ===================================================================================

	public JProperties(String name) throws IOException {
		load(new FileInputStream(name));
	}

	public JProperties(File file) throws IOException {
		load(new FileInputStream(file));
	}

	public JProperties(InputStream inStream) throws IOException {
		load(inStream);
	}

	public JProperties(Reader reader) throws IOException {
		load(reader);
	}

	// ===================================================================================

	public void load(String name) throws IOException {
		load(new FileInputStream(name));
	}

	public void load(File file) throws IOException {
		load(new FileInputStream(file));
	}

	@Override
	public void load(InputStream inStream) throws IOException {
		try {
			super.load(inStream);
		} finally {
			inStream.close();
		}
	}

	@Override
	public void load(Reader reader) throws IOException {
		try {
			super.load(reader);
		} finally {
			reader.close();
		}
	}

	// ===================================================================================

	@Override
	public String getProperty(String key) {
		String property = super.getProperty(key);

		if (property == null) {
			if (_warn)
				_log.warn("DaProperties: Missing property for key - " + key);

			return null;
		}

		return property.trim();
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		String property = super.getProperty(key, defaultValue);

		if (property == null) {
			if (_warn)
				_log.warn("DaProperties: Missing defaultValue for key - " + key);

			return null;
		}

		return property.trim();
	}
}