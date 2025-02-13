package br.com.gabriel.analyzer;

import br.com.gabriel.analyzer.events.Event;
import br.com.gabriel.analyzer.events.EventAction;
import br.com.gabriel.analyzer.events.EventMetadata;
import br.com.gabriel.analyzer.events.EventSchema;
import br.com.gabriel.analyzer.events.ProponentEvent;
import br.com.gabriel.analyzer.events.ProposalEvent;
import br.com.gabriel.analyzer.events.WarrantyEvent;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import static br.com.gabriel.analyzer.events.EventAction.ADDED;
import static br.com.gabriel.analyzer.events.EventAction.CREATED;
import static br.com.gabriel.analyzer.events.EventSchema.PROPONENT;
import static br.com.gabriel.analyzer.events.EventSchema.PROPOSAL;
import static br.com.gabriel.analyzer.events.EventSchema.WARRANTY;

public final class ProposalTestUtils {

  static final String PROPOSAL_ID = "80921e5f-4307-4623-9ddb-5bf826a31dd7";

  private static final Event VALID_PROPOSAL_EVENT = new ProposalEvent(
    anEvent(PROPOSAL, CREATED),
    PROPOSAL_ID,
    1_141_424.0,
    150
  );

  private static final Event LOWER_LOAN_VALUE_PROPOSAL_EVENT = new ProposalEvent(
    anEvent(PROPOSAL, CREATED),
    PROPOSAL_ID,
    29_999.99,
    150
  );
  private static final Event HIGHER_LOAN_VALUE_PROPOSAL_EVENT = new ProposalEvent(
    anEvent(PROPOSAL, CREATED),
    PROPOSAL_ID,
    3_000_001.0,
    150
  );

  public static EventMetadata anEvent(final EventSchema schema, final EventAction action) {
    return new EventMetadata(
      UUID.randomUUID().toString(),
      schema,
      action,
      LocalDateTime.now()
    );
  }

  public interface EventStubBuilder {
    static EventStubBuilder builder() {
      return new EventStubBuilderImpl();
    }
    EventStubBuilder addValidProposalEvent();
    EventStubBuilder addProposalEvent(Double loanValue, Integer numberOfMonthlyInstallments);
    EventStubBuilder addLowerLoanValueInvalidProposalEvent();
    EventStubBuilder addHigherLoanValueInvalidProposalEvent();
    EventStubBuilder addWarrantyEvent(double value, String province);
    EventStubBuilder addWarrantyEvent(EventMetadata event, String id, double value, String province);
    EventStubBuilder addProponentEvent(
      String name,
      int age,
      double monthlyIncome,
      boolean isMain
    );
    List<Event> build();
  }

  private static class EventStubBuilderImpl implements EventStubBuilder {

    private static final Logger LOGGER = Logger.getLogger("aLogger");
    private final List<Event> events = new ArrayList<>();

    @Override public EventStubBuilder addValidProposalEvent() {
      this.addEvent(VALID_PROPOSAL_EVENT);
      return this;
    }

    @Override public EventStubBuilder addProposalEvent(final Double loanValue, final Integer numberOfMonthlyInstallments) {
      this.addEvent(new ProposalEvent(
        anEvent(PROPOSAL, CREATED),
        PROPOSAL_ID,
        loanValue,
        numberOfMonthlyInstallments
      ));
      return this;
    }

    @Override public EventStubBuilder addLowerLoanValueInvalidProposalEvent() {
      this.events.add(LOWER_LOAN_VALUE_PROPOSAL_EVENT);
      return this;
    }

    @Override public EventStubBuilder addHigherLoanValueInvalidProposalEvent() {
      this.addEvent(HIGHER_LOAN_VALUE_PROPOSAL_EVENT);
      return this;
    }

    @Override public EventStubBuilder addWarrantyEvent(final double value, final String province) {
      this.addEvent(new WarrantyEvent(
        anEvent(WARRANTY, ADDED),
        PROPOSAL_ID,
        UUID.randomUUID().toString(),
        value,
        province
      ));
      return this;
    }

    @Override public EventStubBuilder addWarrantyEvent(
      final EventMetadata event,
      final String id,
      final double value,
      final String province
    ) {
      this.events.add(new WarrantyEvent(
        event,
        PROPOSAL_ID,
        id,
        value,
        province
      ));
      return this;
    }

    @Override public EventStubBuilder addProponentEvent(
      final String name,
      final int age,
      final double monthlyIncome,
      final boolean isMain
    ) {
      this.addEvent(new ProponentEvent(
        anEvent(PROPONENT, ADDED),
        PROPOSAL_ID,
        UUID.randomUUID().toString(),
        name,
        age,
        monthlyIncome,
        isMain
      ));
      return this;
    }

    @Override public List<Event> build() {
      return Collections.unmodifiableList(this.events);
    }

    private void addEvent(final Event event) {
      LOGGER.info(MessageFormat.format("Adding an event: {0}", event));
      this.events.add(event);
    }
  }

}
