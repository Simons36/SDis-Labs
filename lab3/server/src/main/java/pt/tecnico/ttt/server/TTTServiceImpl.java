package pt.tecnico.ttt.server;

import io.grpc.stub.StreamObserver;
import pt.tecnico.ttt.*;
import static io.grpc.Status.INVALID_ARGUMENT;

public class TTTServiceImpl extends TTTGrpc.TTTImplBase {

	/** Game implementation. */
	private TTTGame ttt = new TTTGame();

	@Override
	public void currentBoard(CurrentBoardRequest request, StreamObserver<CurrentBoardResponse> responseObserver) {
		// StreamObserver is used to represent the gRPC stream between the server and
		// client in order to send the appropriate responses (or errors, if any occur).

		CurrentBoardResponse response = CurrentBoardResponse.newBuilder().setBoard(ttt.toString()).build();

		// Send a single response through the stream.
		responseObserver.onNext(response);
		// Notify the client that the operation has been completed.
		responseObserver.onCompleted();
	}

	@Override
	public void playResult(PlayResultRequest request, StreamObserver<PlayResultResponse> responseObserver) {
		PlayResult playResultResponse = ttt.play(request.getRow(), request.getColumn(), request.getPlayer());

		if (playResultResponse == PlayResult.OUT_OF_BOUNDS) {
			responseObserver
					.onError(INVALID_ARGUMENT.withDescription("Input has to be a valid position").asRuntimeException());
		} else {
			PlayResultResponse response = PlayResultResponse.newBuilder().setPlResult(playResultResponse).build();

			responseObserver.onNext(response);

			responseObserver.onCompleted();
		}
	}

	@Override
	public void checkWinner(CheckWinnerRequest request, StreamObserver<CheckWinnerResponse> responseObserver) {
		int winnerResponse = ttt.checkWinner();

		CheckWinnerResponse response = CheckWinnerResponse.newBuilder().setWinner(winnerResponse).build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

}
