package com.RegressionTestSelectionTool.xmlfields.dependencies;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

@XStreamAlias("outbound")
@XStreamConverter(value= ToAttributedValueConverter.class, strings={"text"})
public class OutboundField {
    @XStreamAsAttribute
    public String type;

    @XStreamAsAttribute
    public String confirmed;

    public String text;

    public Boolean isConfirmed() {
		if(this.confirmed.equals("yes")) {
			return Boolean.TRUE;
		}else {
			return Boolean.FALSE;
		}
    }
    
    private void cleanInnerClass() {
    	if(this.text.contains("$")) {
    		this.text = this.text.split("\\$")[0];
    	}
    }
    

    public void cleanOutbound() {
    	this.cleanInnerClass();
    }

}
