interface Policy {
    PN.Transitions getBufferTransition();
}

class SharedLoadPolicy implements Policy {

	private final PN mPN;
	private final PN.Transitions bestCPU;

	SharedLoadPolicy(PN Pn) {
		mPN = Pn;
		//noinspection ConstantConditions
		if (Main.SERVICE_RATE_1_ALFA >= Main.SERVICE_RATE_2_ALFA) {
			bestCPU = PN.Transitions.START_BUFFER_2;
		}
		else {
			bestCPU = PN.Transitions.START_BUFFER_1;
		}
	}

	public PN.Transitions getBufferTransition() {
		int buffer1Tokens = mPN.getPlaceTokens(PN.Places.CORE1_BUFFER);
		int buffer2Tokens = mPN.getPlaceTokens(PN.Places.CORE2_BUFFER);

		if (buffer1Tokens == buffer2Tokens) {
			return bestCPU;
		}

		return (buffer1Tokens < buffer2Tokens)
				? PN.Transitions.START_BUFFER_1
				: PN.Transitions.START_BUFFER_2;

	}
}
