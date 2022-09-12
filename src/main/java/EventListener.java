public interface EventListener {

    void onConnectionReady(UserConnection userConnection);
    void onReceiveMessage(UserConnection userConnection, String value);
    void onDisconnect(UserConnection userConnection);
    void onException(UserConnection userConnection, Exception e);

}
