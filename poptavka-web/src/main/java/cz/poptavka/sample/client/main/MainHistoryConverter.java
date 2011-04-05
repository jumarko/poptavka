package cz.poptavka.sample.client.main;

import java.util.logging.Logger;

import com.mvp4g.client.annotation.History;
import com.mvp4g.client.annotation.History.HistoryConverterType;
import com.mvp4g.client.history.HistoryConverter;

/**
 * Basic History Management.
 *
 * @author Beho
 */

@History(type = HistoryConverterType.NONE)
public class MainHistoryConverter implements HistoryConverter<MainEventBus> {

    private static final Logger LOGGER = Logger.getLogger(MainHistoryConverter.class.getName());

    @Override
    public void convertFromToken(String historyName, String param,
            MainEventBus eventBus) {
        LOGGER.fine("historyName: " + historyName);
        LOGGER.fine("param: " + param);
    }

    @Override
    public boolean isCrawlable() {
        return true;
    }





}
