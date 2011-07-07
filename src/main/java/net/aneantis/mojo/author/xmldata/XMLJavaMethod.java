/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.aneantis.mojo.author.xmldata;

import com.thoughtworks.qdox.model.JavaMethod;
import org.codehaus.plexus.util.StringUtils;

/**
 *
 * @author flgourie
 */
public class XMLJavaMethod {
    private final String signature;
    private final int lineNumber;
    private final int lineCount;

    XMLJavaMethod(JavaMethod method) {
        signature=method.getCallSignature();
        lineNumber=method.getLineNumber();
        lineCount=StringUtils.countMatches(method.getSourceCode(),"\n");
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof XMLJavaMethod)) return false;
        XMLJavaMethod xjm = (XMLJavaMethod)obj;
        return this.signature.equals(xjm.signature) && this.lineCount==xjm.lineCount && this.lineNumber==xjm.lineNumber;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public int getLineCount() {
        return lineCount;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getSignature() {
        return signature;
    }
}
