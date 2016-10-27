package full;

import org.junit.Test;
import org.matrix.framework.core.collection.converter.XMLConvert;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by pktczwd on 2016/10/27.
 */
public class MavenCommandGenerator {

    /**
     * modify the following dependency with http://mvnrepository.com/
     */
    private String xml = "<dependency>\n" +
            "    <groupId>org.jooq</groupId>\n" +
            "    <artifactId>jooq</artifactId>\n" +
            "    <version>3.7.4</version>\n" +
            "</dependency>";


    private XMLConvert xmlConvert = new XMLConvert();
    private MavenDependency mavenDependency = xmlConvert.fromString(MavenDependency.class, xml);


    /**
     * install jar and source.
     */
    @Test
    public void test01() {
        System.out.println("mvn install:install-file -DgroupId=" + mavenDependency.getGroupId() + " -DartifactId=" + mavenDependency.getArtifactId() + " -Dversion=" + mavenDependency.getVersion() + " -Dpackaging=jar -Dfile=" + mavenDependency.getArtifactId() + "-" + mavenDependency.getVersion() + ".jar");
        System.out.println("mvn install:install-file -DgroupId=" + mavenDependency.getGroupId() + " -DartifactId=" + mavenDependency.getArtifactId() + " -Dversion=" + mavenDependency.getVersion() + " -Dpackaging=jar -Dfile=" + mavenDependency.getArtifactId() + "-" + mavenDependency.getVersion() + "-source.jar -DgeneratePom=true -Dclassifier=sources");
    }

    @XmlRootElement(name = "dependency")
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class MavenDependency {
        private String groupId;
        private String artifactId;
        private String version;

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getArtifactId() {
            return artifactId;
        }

        public void setArtifactId(String artifactId) {
            this.artifactId = artifactId;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }


}
