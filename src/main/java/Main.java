import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.EPDeployException;
import com.espertech.esper.runtime.client.EPDeployment;
import com.espertech.esper.runtime.client.EPRuntime;
import com.espertech.esper.runtime.client.EPRuntimeProvider;
import com.espertech.esper.runtime.client.EPStatement;
import com.espertech.esper.runtime.client.UpdateListener;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.varia.NullAppender;

public class Main {
    static EPCompiler epCompiler;
    static CompilerArguments compilerArguments;
    static EPRuntime runtime;
    static UpdateListener printListener;

    public static void main(String[] args) {

        Mqtt5BlockingClient client = ConectBroker.conectMqtt();


        BasicConfigurator.configure(new NullAppender());
        epCompiler = EPCompilerProvider.getCompiler();
        Configuration configuration = new Configuration();
        configuration.getCommon().addEventType(PatientSensors.class);
        compilerArguments = new CompilerArguments(configuration);
        runtime = EPRuntimeProvider.getDefaultRuntime(configuration);
        printListener = new UpdateListener() {


            public void update(EventBean[] newData, EventBean[] oldData, EPStatement epStatement, EPRuntime epRuntime) {
                EventBean eventBean = newData[0];
                String nomeRegraEpl = epStatement.getName();

                String topic = "";
                String payload = "";

                if(nomeRegraEpl.equals("sellect-resp_y")) {
                    topic = String.format("topic/%s", nomeRegraEpl.split("-")[1]);

                    payload = String.format("%s", eventBean.get("resp"));

                } else if(nomeRegraEpl.equals("sellect-resp_r")) {
                    topic = String.format("topic/%s", nomeRegraEpl.split("-")[1]);

                    payload = String.format("%s", eventBean.get("resp"));


                } else if(nomeRegraEpl.equals("sellect-verde")) {
                    topic = String.format("topic/%s", nomeRegraEpl.split("-")[1]);

                    payload = String.format("%s", eventBean.get("resp"));


                }
                ConectBroker.publishw(client, topic, payload);

            }
        };
        compileAndDeploy("sellect-resp_y", "select resp from PatientSensors((PatientSensors.resp >= 9) and (PatientSensors.resp < 12))");
        compileAndDeploy("sellect-resp_r", "select resp from PatientSensors((PatientSensors.resp < 9))");
        compileAndDeploy("sellect-spo2_y", "select spo2 from PatientSensors((PatientSensors.spo2 >= 91) and (PatientSensors.spo2 < 96))");
        compileAndDeploy("sellect-spo2_r", "select spo2 from PatientSensors((PatientSensors.spo2 < 91))");
        compileAndDeploy("sellect-hr_y", "select spo2 from PatientSensors((PatientSensors.hr >= 41) and (PatientSensors.hr < 51))");
        compileAndDeploy("sellect-hr_r", "select spo2 from PatientSensors((PatientSensors.hr < 40))");
        List<PatientSensors> list = getPatientSensors();
        Iterator var3 = list.iterator();

        while (var3.hasNext()) {
            PatientSensors patientSensors = (PatientSensors) var3.next();
            System.out.println(patientSensors);
            runtime.getEventService().sendEventBean(patientSensors, "PatientSensors");
        }


    }

    private static void compileAndDeploy(String label, String epl) {
        EPCompiled compiledRule = null;

        try {
            compiledRule = epCompiler.compile("@name('" + label + "') " + epl, compilerArguments);
        } catch (EPCompileException var6) {
            var6.printStackTrace();
        }

        EPDeployment deployment = null;

        try {
            deployment = runtime.getDeploymentService().deploy(compiledRule);
        } catch (EPDeployException var5) {
            var5.printStackTrace();
        }

        EPStatement statement = runtime.getDeploymentService().getStatement(deployment.getDeploymentId(), label);
        statement.addListener(printListener);
    }
    //LEITURA DO BANCO DE DADOS
    public static List<PatientSensors> getPatientSensors() {
        ArrayList listPatientSensors = new ArrayList();

        try {
            FileReader fileReader = new FileReader("x21200008.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = bufferedReader.readLine();

            for(StringBuilder valoresConcatenados = new StringBuilder(); line != null; line = bufferedReader.readLine()) {
                String atributo = line.split(" ")[0].trim();
                if (atributo.equals("RESP")) {
                    if (valoresConcatenados.length() > 0) {
                        String[] valoresAtributos = valoresConcatenados.toString().split(";");
                        PatientSensors patientSensors = new PatientSensors(Integer.valueOf(valoresAtributos[1]),
                                Integer.valueOf(valoresAtributos[2]), Integer.valueOf(valoresAtributos[3]),
                                Integer.valueOf(valoresAtributos[4]));
                        listPatientSensors.add(patientSensors);
                    }

                    valoresConcatenados = new StringBuilder();
                }

                if (!atributo.equals("PAP") && !atributo.equals("ABP")) {
                    int inicioSubString = line.split(" ")[0].length();
                    String valorSensor = line.substring(inicioSubString).trim();
                    valoresConcatenados.append(";").append(valorSensor);
                }
            }
        } catch (Exception var8) {
            var8.printStackTrace();
        }

        return listPatientSensors;
    }

}