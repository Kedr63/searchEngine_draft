package searchengine.services.factoryObject;

import lombok.Data;
import org.mockito.Mockito;
import searchengine.config.SnippetSearcherConfiguration;
import searchengine.config.UserAgentList;
import searchengine.services.PoolService;
import searchengine.services.PoolServiceImpl;
import searchengine.services.indexEntityService.IndexEntityServiceImpl;
import searchengine.services.lemmaService.LemmaServiceImpl;
import searchengine.services.pageService.PageServiceImp;
import searchengine.services.siteService.SiteServiceImp;

@Data
public class PoolServiceMockitoFactory {

   public static PoolService getPoolService(){
        return (PoolService) new PoolServiceImpl(Mockito.mock(SiteServiceImp.class), Mockito.mock(PageServiceImp.class),
                Mockito.mock(LemmaServiceImpl.class), Mockito.mock(IndexEntityServiceImpl.class), Mockito.mock(UserAgentList.class),
                Mockito.mock(SnippetSearcherConfiguration.class));
    }


}
