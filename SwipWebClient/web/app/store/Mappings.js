Ext.data.proxy.Rest.override({
    type: 'rest',
    actionMethods : {
        create : 'POST',
        read : 'GET',
        update : 'GET',
        destroy: 'POST'
    }
});

Ext.define('SWIP.store.Mappings', {
    extend    : 'Ext.data.Store',
    singleton : true,
    model     : 'SWIP.model.Mapping',
    storeId: 'Mappings',
    autoLoad: false,
    autoSync: false,
    restful: true,
    proxy:  {
        type: 'rest',
        api: {
            read    : '/SwipWorkflow/resources/mappings/{plquery}'
        //   update  : 'resources/swip/mappings/plquery/{id}'
        //            
        }, 
        //        url: 'resources/swip/mappings/{plquery}',    

        headers: {
            'Content-type': 'application/json'
        },
        reader: new Ext.data.JsonReader({
            root:'result',
            totalProperty:'totalCount',
            successProperty: 'success'
        },
        
        [
        //        {
        //            name: 'id'
        //        },
        {
            name: 'sparqlQuery'
        },
        {
            name: 'descriptiveSentence'
        },
        {
            name: 'mappingDescription'
        },
        {
            name: 'relevanceMark'
        },
        {
            name: 'validate'
        }
        ])
    //        writer: new Ext.data.JsonWriter({
    //            writeAllFields: true,
    //             encode: true
    //       
    //        })
        
    },
    constructor : function() {
        this.callParent(arguments);
    }
   
});


