class Policy {

	private PN mPN;

	Policy(PN PN) {
		mPN = PN;
	}

	public PN.Transitions getBufferTransition() {
		int buffer1Tokens = mPN.getPlaceTokens(PN.Places.Buffer1);
		int buffer2Tokens = mPN.getPlaceTokens(PN.Places.Buffer2);

		return (buffer1Tokens > buffer2Tokens)
				? PN.Transitions.START_BUFFER_2
				: PN.Transitions.START_BUFFER_1;
	}
}
