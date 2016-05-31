package IoT;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;

public class SeismicSensorHandler {

    public static int generateGraph(int action){
        boolean failed = false;
        FileWriter fileWriter = null;
        if(action == 0){
            try {
                fileWriter = new FileWriter("/home/hadoop/IdeaProjects/Blaze-IoT-Analytics/web/content/CSV/seismicIntensity.csv");
                fileWriter.append("name,val,lat,lon");
                fileWriter.append("\n");
                try {
                    String ret1 = HiveQueryExecutor.executeQuery("DROP TABLE IF EXISTS seismicTable");
                    String ret2 = HiveQueryExecutor.executeQuery("CREATE TABLE seismicTable (json STRING)");
                    String ret3 = HiveQueryExecutor.executeQuery("LOAD DATA LOCAL INPATH '/home/hadoop/uploads/JSON/file_seismic.json' INTO TABLE seismicTable");
                    String value = HiveQueryExecutor.executeQuery("SELECT get_json_object(seismicTable.json, \"$.location.lat\"), get_json_object(seismicTable.json, \"$.location.lon\"), get_json_object(seismicTable.json, \"$.value\"), get_json_object(seismicTable.json, \"$.location.state\"), get_json_object(seismicTable.json, \"$.location.street\") FROM seismicTable");
                    String[] tokens = value.split("<br/>");
                    float num = 0;
                    for(int i = 0; i < tokens.length; i++){
                        String[] val = tokens[i].split(" ");
                        fileWriter.append(val[3]);
                        fileWriter.append(",");
                        num = Float.parseFloat(val[2]);
                        num = num * 80000;
                        fileWriter.append(String.valueOf(num));
                        fileWriter.append(",");
                        fileWriter.append(val[0]);
                        fileWriter.append(",");
                        fileWriter.append(val[1]);
                        fileWriter.append("\n");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    failed = true;
                }


            } catch (IOException e) {
                e.printStackTrace();
                failed = true;
            } finally {
                try {
                    fileWriter.flush();
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    failed = true;
                }

                if(failed){
                    return -1;
                }
            }

        }
        return 0;
    }
}