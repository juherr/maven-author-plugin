/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.aneantis.mojo.author.xmldata;

import com.thoughtworks.qdox.model.JavaMethod;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.maven.model.Developer;

/**
 *
 * @author flgourie
 */
public class XMLFileInfo {
    private Set<Developer> mainAuthors = new HashSet<Developer>();
    private Map<XMLJavaMethod, Set<Developer>> methodAuthors = new HashMap<XMLJavaMethod, Set<Developer>>();

    public void addMainAuthors(Set<Developer> dev) {
        mainAuthors.addAll(dev);
    }

    public void addMethodAuthors(JavaMethod method, Set<Developer> authors) {
        XMLJavaMethod xjm = new XMLJavaMethod(method);
        Set<Developer> set = methodAuthors.get(xjm);
        if(set==null) {
            methodAuthors.put(xjm, authors);
        } else {
            set.addAll(authors);
        }
    }

    public Set<Developer> getMainDevelopers() {
        return mainAuthors;
    }

    public Map<XMLJavaMethod,Set<Developer>> getMethodDevelopers() {
        return methodAuthors;
    }
}
