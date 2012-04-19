Ext.define('SWIP.model.Mapping', {
    extend   : 'Ext.data.Model',
    fields: [
//    {
//        name: 'id',
//        type: 'number'
//    }, 
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
        name: 'sparqlQuery' 
    },
    {
        name: 'validate',
        type: 'bool'
    }
    ]


//    changeQuery: function(newQuery) {
//        
//        this.set('query', newQuery);
//    },
//    
//    
//    setCurrentDate: function() {
//        
//        this.set('searchDate', new Date());
//    }    
});


