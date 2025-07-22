package guru.qa.niffler.test.grpc;

import com.google.protobuf.Empty;
import guru.qa.niffler.grpc.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Stream;

import static guru.qa.niffler.model.spend.CurrencyValues.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CurrencyGrpcTest extends BaseGrpcTest {

    @Nonnull
    private static Stream<Arguments> currencyConversionProvider() {
        return Stream.of(
                Arguments.of(RUB, USD, 200.0, 3.0),
                Arguments.of(USD, EUR, 75.0, 69.45),
                Arguments.of(RUB, EUR, 150.0, 2.085)
        );
    }

    @ParameterizedTest
    @MethodSource("currencyConversionProvider")
    void shouldCalculateRatesF(CurrencyValues spendCurrency,
                               CurrencyValues desiredCurrency,
                               double amount,
                               double calculatedAmount) {
        final CalculateRequest request = CalculateRequest.newBuilder()
                .setSpendCurrency(spendCurrency)
                .setDesiredCurrency(desiredCurrency)
                .setAmount(amount)
                .build();

        final CalculateResponse response = blockingStub.calculateRate(request);
        assertEquals(calculatedAmount, response.getCalculatedAmount());
    }

    @Test
    void allCurrenciesShouldBeReturned(){
        final CurrencyResponse allCurrencies = blockingStub.getAllCurrencies(Empty.getDefaultInstance());
        List<Currency> allCurrenciesList = allCurrencies.getAllCurrenciesList();
        Assertions.assertEquals(4, allCurrenciesList.size());
        assertEquals(4, allCurrenciesList.size());
    }
}
