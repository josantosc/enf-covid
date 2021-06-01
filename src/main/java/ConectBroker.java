import com.hivemq.client.mqtt.MqttClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import static com.hivemq.client.mqtt.MqttGlobalPublishFilter.ALL;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ConectBroker {

    public static Mqtt5BlockingClient conectMqtt(){
    final String host = "2cf8e89d6982403db810ec0e1ddae610.s1.eu.hivemq.cloud";
    final String username = "monitorcovid";
    final String password = "Monitor@123";

    //create an MQTT client
    final Mqtt5BlockingClient client = MqttClient.builder()
            .useMqttVersion5()
            .serverHost(host)
            .serverPort(8883)
            .sslWithDefaultConfig()
            .buildBlocking();

    //connect to HiveMQ Cloud with TLS and username/pw
        client.connectWith()
                .simpleAuth()
                .username(username)
                .password(UTF_8.encode(password))
            .applySimpleAuth()
                .send();

        System.out.println("Connected successfully");

        return  client;
}
    public static void subscribe(Mqtt5BlockingClient client, String infos) {
        client.subscribeWith()
                .topicFilter(infos)
                .send();
    }

    public static void publishw(Mqtt5BlockingClient client, String topico, String msg){
        client.publishWith()
                .topic(topico)
                .payload(UTF_8.encode(msg))
                .send();
    }

}