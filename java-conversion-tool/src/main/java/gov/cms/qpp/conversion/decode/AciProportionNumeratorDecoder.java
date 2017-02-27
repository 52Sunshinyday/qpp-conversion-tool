package gov.cms.qpp.conversion.decode;

import org.jdom2.Element;

import gov.cms.qpp.conversion.model.Node;
import gov.cms.qpp.conversion.model.XmlDecoder;

@XmlDecoder(templateId="2.16.840.1.113883.10.20.27.3.31")
public class AciProportionNumeratorDecoder extends QppXmlDecoder {
	@Override
	protected Node internalDecode(Element element, Node thisnode) {
		return this.decode(element.getChild("entryRelationship", defaultNs), thisnode);
	}
}
