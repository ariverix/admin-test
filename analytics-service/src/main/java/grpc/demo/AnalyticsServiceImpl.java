package grpc.demo;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class AnalyticsServiceImpl extends AnalyticsServiceGrpc.AnalyticsServiceImplBase {

    @Override
    public void calculateUserRating(UserRatingRequest request, StreamObserver<UserRatingResponse> responseObserver) {
        // Имитация бурной деятельности и сложных расчетов
        int score = (int) (Math.random() * 100);
        String verdict = score > 50 ? "GOOD" : "BAD";

        UserRatingResponse response = UserRatingResponse.newBuilder()
                .setUserId(request.getUserId())
                .setRatingScore(score)
                .setVerdict(verdict)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
