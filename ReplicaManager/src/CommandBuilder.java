/**
 * Created by Aymeric on 2015-11-29.
 */
public class CommandBuilder {

    private String modulePathReplica;
    private String modulePathShared = "../../out/production/Shared";
    private String classPath;
    private String mainArgument;
    private String implementationName;
    private String modulePathSequencer = "../../out/production/Sequencer";
    private String rudplibs = "";
    private String separator;
    private String cmdCommand;


    public CommandBuilder setImplementation(String studentName){

        this.separator = ";";
        String os = System.getProperty("os.name");
        this.cmdCommand = "cmd /c start";

        if (os.contains("Mac OS")){
            this.separator = ":";
            this.cmdCommand = "";
        }

        System.err.println(os);

        this.implementationName = studentName;

        this.modulePathReplica =  String.format("../../out/production/Replica_%1$s", implementationName);

        switch (studentName.toLowerCase())
        {
            case "aymeric":
                this.classPath = "Server.StartBankServer";
                break;
            case "pascal":
                this.classPath = "dlms.StartBankServer";
                break;
            case "richard":
                this.classPath = "impl.InitServers";
                this.rudplibs = this.separator + "../../libs/rudp/classes";
                break;
            case "mathieu":
                this.classPath = "dlms.replica.ReplicaLauncher";
                break;
        }
        return this;
    }

    public CommandBuilder setBank(String bankName)
    {
        this.mainArgument = bankName;
        return this;
    }

    public String getCommand(){

        return String.format(
                "%7$s java -cp %1$s%2$s%3$s%2$s%4$s%8$s %5$s %6$s",
                modulePathReplica,
                separator,
                modulePathShared,
                modulePathSequencer,
                classPath,
                mainArgument,
                cmdCommand,
                rudplibs
        );
    }

}
