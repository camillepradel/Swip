Ext.define('SWIP.store.HistoryRecords', {
    extend    : 'Ext.data.Store',
    model     : 'SWIP.model.HistoryRecord',
    storeId   : 'HistoryRecords',
    autoLoad  : true,
    autoSync  : false,
    proxy     : {
                    type: 'localstorage',
                    id : 'local-id'
                },
    constructor : function() {
        this.callParent(arguments);
    }
});

