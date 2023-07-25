package com.RegressionTestSelectionTool.xmlfields.dependencies;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.HashSet;

@XStreamAlias("class")
public class ClassField {
    @XStreamAsAttribute
    public String confirmed;

    public String name;

    @XStreamImplicit
    @XStreamAlias("inbound")
    public HashSet<InboundField> inbounds = new HashSet<>();

    @XStreamImplicit
    @XStreamAlias("outbound")
    public HashSet<OutboundField> outbounds = new HashSet<>();
    
    
    public void cleanBounds() {
    	
    	this.cleanInnerClass();
    	
    	
    	if(this.inbounds != null) {

    		for(InboundField inboundField : this.inbounds){
        		inboundField.cleanInbound();
        	}
        	
    		cleanDuplicateInbounds();
    	}
    	
    	if(this.outbounds != null) {
 		
    		for(OutboundField outboundField : this.outbounds){
        		outboundField.cleanOutbound();
        	}
        	
    		cleanDuplicateOutbounds();
    	}

    	
    	this.cleanOwnReferenceBounds();
    	
    	
    }
    
    public Boolean isConfirmed() {
		if(this.confirmed.equals("yes")) {
			return Boolean.TRUE;
		}else {
			return Boolean.FALSE;
		}
    }
    
    private void cleanInnerClass() {
    	if(this.name.contains("$")) {
    		this.name = this.name.split("\\$")[0];
    	}
    }
    
    private void cleanDuplicateInbounds() {
    	
    	HashSet<InboundField> inboundsToRemove = new HashSet<InboundField>(); 
    	
    	if(this.inbounds != null) {
    		
    		for(InboundField inboundField : this.inbounds) {
        		String name = inboundField.text;
        		int count = 0;
        		
        		for(InboundField inboundField2 : this.inbounds) {
        			String name2 = inboundField2.text;
        			
        			if(name2.equals(name)) {
        				count++;
        				
        				if(count > 1) {
            				inboundsToRemove.add(inboundField2);
            				count = 1;
            			}
        			}
        		}	
        	}	
        	
    		this.inbounds.removeAll(inboundsToRemove);
    	}
    }
    
    private void cleanDuplicateOutbounds() {
    	
    	HashSet<OutboundField> outboundsToRemove = new HashSet<OutboundField>(); 
    	
    	if(this.outbounds != null) {
    		
    		for(OutboundField outboundField : this.outbounds) {
        		String name = outboundField.text;
        		int count = 0;
        		
        		for(OutboundField outboundField2 : this.outbounds) {
        			String name2 = outboundField2.text;
        			
        			if(name2.equals(name)) {
        				count++;
        				
        				if(count > 1) {
        					outboundsToRemove.add(outboundField2);
            				count = 1;
            			}
        			}
        		}	
        	}	
        	
    		this.outbounds.removeAll(outboundsToRemove);
    	}
    }
    
    public void cleanOwnReferenceBounds() {
    	
    	HashSet<InboundField> inboundsToRemove = new HashSet<InboundField>();
    	HashSet<OutboundField> outboundsToRemove = new HashSet<OutboundField>();
    	
    	if(this.inbounds != null) {
    		for(InboundField inboundField : this.inbounds) {
    			if(this.name.equals(inboundField.text)) {
    				inboundsToRemove.add(inboundField);
    			}
    		}
    		this.inbounds.removeAll(inboundsToRemove);
    	}
    	
    	if(this.outbounds != null) {
    		for(OutboundField outboundField : this.outbounds) {
    			if(this.name.equals(outboundField.text)) {
    				outboundsToRemove.add(outboundField);
    			}
    		}
    		this.outbounds.removeAll(outboundsToRemove);
    	}
    	
    	
    }
    
    //A classe pode referenciar um pacote no XML? Parece que não
    
}
