package no.difi.meldingsutveksling.receipt

import no.difi.meldingsutveksling.ServiceIdentifier
import no.difi.meldingsutveksling.receipt.strategy.NoOperationStrategy
import org.springframework.beans.factory.ObjectProvider
import org.springframework.stereotype.Component

@Component
class StatusStrategyFactory(strategies: ObjectProvider<StatusStrategy>) {

    private val statusStrategies = mutableMapOf<ServiceIdentifier, StatusStrategy>()

    init {
        strategies.orderedStream().forEach { statusStrategies[it.serviceIdentifier] = it }
    }

    fun getStrategy(si: ServiceIdentifier): StatusStrategy {
        return statusStrategies.getOrDefault(si, NoOperationStrategy())
    }
}