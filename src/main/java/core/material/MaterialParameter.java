package core.material;

import core.shader.ShaderVariable;

public class MaterialParameter{

	protected String name;
	protected Object value;
	protected ShaderVariable.VariableType type;

	MaterialParameter(String name, Object value, ShaderVariable.VariableType type ) {
		this.name = name;
		this.value = value;
		this.type = type;
	}

	public String getName() {
		return this.name;
	}

	public Object getValue() {
		return value;
	}

	public ShaderVariable.VariableType getType() {
		return type;
	}

}

