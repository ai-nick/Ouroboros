package HelpingHand;

import OuroborosAgent.FitnessBlock;
import OuroborosAgent.FitnessBlock.FitnessBlock_TYPE;
import OuroborosAgent.FitnessBlock.FitnessBlockBuilder;

public class FitnessBlock {
    private static final long serialVersionUID = 1L;

    int sender;
    int receiver;
    FitnessBlock_TYPE type;
    String FitnessBlock_body;

    public enum FitnessBlock_TYPE {
        READY, EVENT_NEW_FITNESS, EVENT_NEW_VALIDATION, EVENT_RANDOM_CHOICE, EVENT_FINISHED_SUBSET, EVENT_HELPING_PEER,
        EVENT_SPECIECIATION_COMPLETE, EVENT_REPRODUCTION_COMPLETE
    }

    @Override
    public String toString() {
        return String.format("FitnessBlock {type=%s, sender=%d, receiver=%d, blocks=%s}", type, sender, receiver, FitnessBlock_body);
    }

    static class FitnessBlockBuilder {
        private final FitnessBlock FitnessBlock = new FitnessBlock();

        FitnessBlockBuilder withSender(final int sender) {
            FitnessBlock.sender = sender;
            return this;
        }

        FitnessBlockBuilder withReceiver(final int receiver) {
            FitnessBlock.receiver = receiver;
            return this;
        }

        FitnessBlockBuilder withType(final FitnessBlock_TYPE type) {
            FitnessBlock.type = type;
            return this;
        }

        FitnessBlockBuilder withBody(final String body) {
            FitnessBlock.FitnessBlock_body = body;
            return this;
        }

        FitnessBlock build() {
            return FitnessBlock;
        }

    }
}
