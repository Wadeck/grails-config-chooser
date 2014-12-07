package grails.plugin.configChooser.popup.test;

import grails.plugin.configChooser.popup.IChoiceValue;

/**
 * @author Wadeck Follonier, wfollonier@proactive-partners.ch
 */
public class ChoiceValueDummy implements IChoiceValue {
	private String name;
	private String content;

	public ChoiceValueDummy(String name, String content){
		this.name = name;
		this.content = content;
	}

	@Override
	public String computeStringRepresentation() {
		return name;
	}

	public String getContent(){
		return content;
	}
}
