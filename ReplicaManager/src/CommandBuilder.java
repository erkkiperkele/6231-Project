/**
 * Created by Aymeric on 2015-11-29.
 */
public class CommandBuilder {

    private String modulePathReplica;
    private String modulePathShared = "./out/production/Shared";
    private String classPath;
    private String mainArgument;
    private String implementationName;
    private String modulePathSequencer = "./out/production/Sequencer";


    public CommandBuilder setImplementation(String studentName){

        //Patch, waiting for Richard's to work
        this.implementationName = studentName.equals("richard")
                ? "aymeric"
                : studentName;

        this.modulePathReplica =  String.format("./out/production/Replica_%1$s", implementationName);

        switch (studentName.toLowerCase())
        {
            case "aymeric":
                this.classPath = "Server.StartBankServer";
                break;
            case "pascal":
                this.classPath = "dlms.StartBankServer";
                break;
            case "richard":
                this.classPath = "Server.StartBankServer";
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


        String os = System.getProperty("os.name");
        String separator = os.contains("Mac OS")
                ? ":"
                : ";";

        System.err.println(os);
//        String separator = System.getProperty("os.name")


        return String.format(
                "java -cp %1$s%2$s%3$s%2$s%4$s %5$s %6$s",
                modulePathReplica,
                separator,
                modulePathShared,
                modulePathSequencer,
                classPath,
                mainArgument
        );
    }

}
