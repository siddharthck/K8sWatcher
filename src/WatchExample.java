
import com.google.gson.reflect.TypeToken;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.CoreV1Event;
import io.kubernetes.client.openapi.models.V1Namespace;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.util.ClientBuilder;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.Watch;
import io.kubernetes.client.util.Watch.Response;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.OkHttpClient;

public class WatchExample {
    public static void main(String[] args) throws IOException, ApiException {
        ApiClient client = ClientBuilder.cluster().build();;
        // infinite timeout
        OkHttpClient httpClient =
            client.getHttpClient().newBuilder().readTimeout(0, TimeUnit.SECONDS).build();
        client.setHttpClient(httpClient);
        Configuration.setDefaultApiClient(client);

        CoreV1Api api = new CoreV1Api();
        Call call = api.listEventForAllNamespacesCall(null, null, null, null, null, null, null, null, 30,  true,null);
        Watch<CoreV1Event> watch = Watch.createWatch(
            client,
            call,
            new TypeToken<Response<CoreV1Event>>(){}.getType());
/*        Watch<V1Namespace> watch =
            Watch.createWatch(
                client,
                api.listNamespaceCall(
                    null, null, null, null, null, 5, null, 300, true, null),
                new TypeToken<Watch.Response<V1Namespace>>() {}.getType());*/

        try {
            System.out.println("Trying to watch");
/*            for (Watch.Response<V1Namespace> item : watch) {
                System.out.printf("%s : %s%n", item.type, item.object.getMetadata().getName());
            }*/
            int i =0;
            for (Response<CoreV1Event> event : watch) {

                System.out.println("got event" + i++);
                System.out.println(event.type);

               // if(i>1)break;
                String msg = event.object.getMessage();
                String invOb = event.object.getInvolvedObject().toString();
                String type = event.object.getType();
                String timestamp = event.object.getLastTimestamp().toString();



                System.out.println(String.format("msg :  %s, involved obj : %s, type : %s, timestamp : %s",msg,invOb,type,timestamp));
            }


        } finally {
            System.out.println("finishing off the watch !");
            watch.close();
        }
    }
}
