Ext.define('SWIP.model.HistoryRecord', {
    extend   : 'Ext.data.Model',
    fields: 
        [{
            name: 'query',
            mapping: 'query'
       },{ 
            name: 'searchDate', 
            type: 'date', 
            dateFormat: 'c',
            mapping: 'searchDate'
        }]
});


