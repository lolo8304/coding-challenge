package redis.resp;

import java.util.Optional;

public interface IResp {
    public Optional<RespResponse> executeCommand(RespRequest request);
}
