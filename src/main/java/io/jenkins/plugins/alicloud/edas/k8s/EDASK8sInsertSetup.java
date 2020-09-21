package io.jenkins.plugins.alicloud.edas.k8s;

import hudson.AbortException;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import io.jenkins.plugins.alicloud.AliCloudCredentials;
import io.jenkins.plugins.alicloud.BaseSetup;
import io.jenkins.plugins.alicloud.BaseSetupDescriptor;
import java.io.IOException;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;
import org.kohsuke.stapler.QueryParameter;

public class EDASK8sInsertSetup extends BaseSetup {
    private String credentialsString;
    private String namespace;
    private String clusterId;
    private String applicationName; // support jenkins variables
    private String targetObject;
    private String packageType;

    private String descFormat;      // support jenkins variables
    private String k8sNamespace = "default";


    // application environment
    private String edasContainerVersion;
    private String webContainer;
    private String jdk;
    private String versionLabelFormat;
    private String envs;
    // start command
    private String startupCommand = "unchanging"; //default unchanging
    private String args;
    // resource quota
    private String cpuLimit;
    private String memoryLimit;
    private String cpuRequest;
    private String memoryRequest;
    private String replicas = "1";
    // application management
    private String postStart = "unchanging";   //default unchanging
    private String preStop = "unchanging";     //default unchanging
    private String readiness = "unchanging";   //default unchanging
    private String liveness = "unchanging";    //default unchanging


    @DataBoundConstructor
    public EDASK8sInsertSetup(
            String credentialsString,
            String namespace,
            String clusterId,
            String k8sNamespace,
            String applicationName,
            String targetObject,
            String packageType,
            String jdk) {

        this.namespace = namespace;
        this.credentialsString = credentialsString;
        this.applicationName = applicationName;
        this.targetObject = targetObject;
        this.packageType = packageType;
        this.clusterId = clusterId;
        this.k8sNamespace = k8sNamespace;
        this.jdk = jdk;
    }

    public String getApplicationName() {
        return applicationName == null ? "" : applicationName;
    }

    public String getCredentialsString() {
        return credentialsString;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener) throws InterruptedException, IOException {
        EDASK8sCreator creator = new EDASK8sCreator(run, workspace, listener, this);

        try {
            boolean result = creator.perform();
            if (!result) {
                throw new AbortException("edas k8s application create failed");
            }
        } catch (AbortException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new AbortException(String.format("edas k8s application create for %s", e.getMessage()));
        }
    }

    public static DescriptorImpl getDesc() {
        return new DescriptorImpl();
    }

    public String getNamespace() {
        return namespace;
    }

    public String getTargetObject() {
        return targetObject;
    }



    public String getEdasContainerVersion() {
        return edasContainerVersion;
    }

    public String getJdk() {
        return jdk;
    }

    public String getVersionLabelFormat() {
        return versionLabelFormat;
    }

    public String getEnvs() {
        return envs;
    }

    public String getStartupCommand() {
        return startupCommand;
    }

    public String getArgs() {
        return args;
    }

    public String getCpuLimit() {
        return cpuLimit;
    }

    public String getMemoryLimit() {
        return memoryLimit;
    }

    public String getCpuRequest() {
        return cpuRequest;
    }

    @DataBoundSetter
    public void setEdasContainerVersion(String edasContainerVersion) {
        this.edasContainerVersion = edasContainerVersion;
    }

    @DataBoundSetter
    public void setVersionLabelFormat(String versionLabelFormat) {
        this.versionLabelFormat = versionLabelFormat;
    }


    @DataBoundSetter
    public void setEnvs(String envs) {
        this.envs = envs;
    }

    @DataBoundSetter
    public void setStartupCommand(String startupCommand) {
        this.startupCommand = startupCommand;
    }

    @DataBoundSetter
    public void setArgs(String args) {
        this.args = args;
    }

    @DataBoundSetter
    public void setCpuLimit(String cpuLimit) {
        this.cpuLimit = cpuLimit;
    }

    @DataBoundSetter
    public void setMemoryLimit(String memoryLimit) {
        this.memoryLimit = memoryLimit;
    }


    @DataBoundSetter
    public void setCpuRequest(String cpuRequest) {
        this.cpuRequest = cpuRequest;
    }

    public String getMemoryRequest() {
        return memoryRequest;
    }

    @DataBoundSetter
    public void setMemoryRequest(String memoryRequest) {
        this.memoryRequest = memoryRequest;
    }

    public String getReplicas() {
        return replicas;
    }

    @DataBoundSetter
    public void setReplicas(String replicas) {
        this.replicas = replicas;
    }

    public String getPostStart() {
        return postStart;
    }

    @DataBoundSetter
    public void setPostStart(String postStart) {
        this.postStart = postStart;

    }

    public String getPreStop() {
        return preStop;
    }

    @DataBoundSetter
    public void setPreStop(String preStop) {
        this.preStop = preStop;
    }

    public String getReadiness() {
        return readiness;
    }

    @DataBoundSetter
    public void setReadiness(String readiness) {
        this.readiness = readiness;

    }

    public String getLiveness() {
        return liveness;
    }

    @DataBoundSetter
    public void setLiveness(String liveness) {
        this.liveness = liveness;

    }

    public String getWebContainer() {
        return webContainer;
    }

    @DataBoundSetter
    public void setWebContainer(String webContainer) {
        this.webContainer = webContainer;
    }

    public String getPackageType() {
        return packageType;
    }

    public String getClusterId() {
        return clusterId;
    }

    public String getDescFormat() {
        return descFormat;
    }

    @DataBoundSetter
    public void setDescFormat(String descFormat) {
        this.descFormat = descFormat;
    }

    public String getK8sNamespace() {
        return k8sNamespace;
    }

    @Extension
    @Symbol("insertK8sApplication")
    public static class DescriptorImpl extends BaseSetupDescriptor {
        @Override
        public String getDisplayName() {
            return "Create EDAS K8s Application";
        }

        public ListBoxModel doFillCredentialsStringItems(@QueryParameter String credentials) {
            ListBoxModel items = new ListBoxModel();
            items.add("");
            for (AliCloudCredentials creds : AliCloudCredentials.getCredentials()) {

                items.add(creds, creds.toString());
                if (creds.toString().equals(credentials)) {
                    items.get(items.size() - 1).selected = true;
                }
            }

            return items;
        }

        public FormValidation doCheckcredentialsString(@QueryParameter String value) {
            if (value.length() == 0) {
                return FormValidation.error("Please choose EDAS Credentials");
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckNamespace(@QueryParameter String value) {
            if (value.length() == 0) {
                return FormValidation.error("Please set Namespace");
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckK8sNamespace(@QueryParameter String value) {
            if (value.length() == 0) {
                return FormValidation.error("Please set K8s Namespace");
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckClusterId(@QueryParameter String value) {
            if (value.length() == 0) {
                return FormValidation.error("Please set Cluster ID");
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckApplicationName(@QueryParameter String value) {
            if (value.length() == 0) {
                return FormValidation.error("Please set Application Name");
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckTargetObject(@QueryParameter String value) {
            if (value.length() == 0) {
                return FormValidation.error("Please set Target Object");
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckPackageType(@QueryParameter String value) {
            if (value.length() == 0) {
                return FormValidation.error("Please set Package Type");
            }
            if (value.equalsIgnoreCase("FatJar")
                || value.equalsIgnoreCase("WAR")
                || value.equalsIgnoreCase("Image")) {
                return FormValidation.ok();
            }
            return FormValidation.error("Please set Package Type as one of FatJar/WAR/Image");
        }

        public FormValidation doCheckJdk(
            @QueryParameter String value,
            @QueryParameter("packageType") String packageType) {
            if ("Image".equalsIgnoreCase(packageType)) {
                return FormValidation.ok();
            }

            if (value.length() == 0) {
                return FormValidation.error("Please set JDK when non-Image packageType");
            }
            return FormValidation.ok();
        }
    }

}
