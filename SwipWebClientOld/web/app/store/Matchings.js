Ext.define('SWIP.store.Matchings', {
    extend    : 'Ext.data.Store',
    singleton : true,
    model     : 'SWIP.model.Matching',
    storeId: 'Matchings',
    autoLoad: false,
    restful: true,
    proxy:  {
        type: 'rest',
        url: 'resources/swip/matchings/{mquery}', 
        headers: {
            'Content-type': 'application/json'
        },
        reader: new Ext.data.JsonReader({
            root: 'responseMatching',
            totalProperty: 'total'
        },
        [{
            name: 'keyWord'
        }, 
        {
            name: 'matchingType'
        },
        {
            name: 'uri'
        },
        {
            name: 'matchingQuerried'
        },
        {
            name: 'matchingMatchedLabel'
        },
        {
            name: 'matchingTrustMark'
        } 
        ])
    },
    groupField: 'keyWord',
    constructor : function() {
        this.callParent(arguments);
    }    
});





