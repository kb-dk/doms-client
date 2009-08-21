package dk.statsbiblioteket.doms;

import java.util.List;

/**
 * TODO abr forgot to document this class
 */
public interface CompoundContentModel {

    public List<ContentModel> getContentModels();



    public List<DatastreamDefinition> getDefinedDatastreams();

    public List<RelationDefinition> getDefinedRelations();
    
    
}
