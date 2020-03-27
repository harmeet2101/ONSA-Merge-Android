//package co.uk.depotnet.onsa.networking;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.fasterxml.jackson.annotation.JsonPropertyOrder;
//
//import java.util.List;
//
//@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonPropertyOrder({
//        "status",
//        "message",
//        "data",
//})
//public class APIResponse<Td> {
//
//    @JsonProperty("status")
//    private boolean status;
//
//    @JsonProperty("message")
//    private String message;
//
//    @JsonProperty("data")
//    private List<Td> data;
//
//    @JsonProperty("status")
//    public boolean getStatus() {
//        return status;
//    }
//
//    @JsonProperty("status")
//    public void setStatus(boolean status) {
//        this.status = status;
//    }
//
//    @JsonProperty("message")
//    public String getMessage() {
//        return message;
//    }
//
//    @JsonProperty("message")
//    public void setMessage(String message) {
//        this.message = message;
//    }
//
//    @JsonProperty("data")
//    public List<Td> getData() {
//        return data;
//    }
//
//    @JsonProperty("data")
//    public void setData(List<Td> data) {
//        this.data = data;
//    }
//}