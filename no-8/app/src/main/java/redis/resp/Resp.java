package redis.resp;

import java.util.Optional;

public class Resp implements IResp {

    public Resp() {

    }

    @Override
    public Optional<RespResponse> executeCommand(RespRequest request) {
        return Optional.empty();
    }

}
