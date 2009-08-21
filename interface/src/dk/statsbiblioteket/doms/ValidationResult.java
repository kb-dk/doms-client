package dk.statsbiblioteket.doms;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: kfc
 * Date: Nov 5, 2008
 * Time: 8:22:05 AM
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name = "validation")

public class ValidationResult {


    private boolean valid;


    private List<String> problems;

    public ValidationResult() {
        valid = true;
        problems = new ArrayList<String>();
    }

    public ValidationResult(boolean valid, List<String> problems) {
        this.valid = valid;
        this.problems = problems;
    }


    @XmlAttribute()
    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    @XmlElement(name = "problem")
    @XmlElementWrapper()
    public List<String> getProblems() {
        return Collections.unmodifiableList(problems);
    }

    public void setProblems(List<String> problems) {
        this.problems = problems;
    }


    public boolean add(String s) {
        return problems.add(s);
    }

    public boolean addAll(Collection<? extends String> strings) {
        return problems.addAll(strings);
    }


    public ValidationResult combine(ValidationResult that) {
        ValidationResult result = new ValidationResult();
        result.setValid(this.isValid() && that.isValid());
        List<String> problems1 = this.getProblems();
        List<String> problems2 = that.getProblems();
        ArrayList<String> newproblems = new ArrayList<String>();
        newproblems.addAll(problems1);
        newproblems.addAll(problems2);
        result.setProblems(newproblems);
        return result;
    }
}
