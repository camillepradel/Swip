Ext.Loader.setConfig({
    enabled: true
});

Ext.Loader.setPath('Ext.ux', 'ext4/examples/ux/');

Ext.define('SWIP.view.search.MappingGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.mappingrid',
    requires: [
    'Ext.data.*',
    'Ext.util.*',
    'Ext.ux.RowExpander',
    'Ext.layout.container.Fit',   
    'Ext.selection.CellModel',
    'Ext.ux.CheckColumn',   
    'SWIP.view.feature.Selectable',
    'SWIP.view.search.SearchPagingToolbar'
    ],
    store: 'Mappings',
    cls: 'search-results',
    autoScroll: true,
    layout : {
        type  : 'fit',
        align : 'stretch',
        pack: 'start'
    },
    features: [ {
        ftype: 'selectable', 
        id: 'selectable'
    } ],
    plugins : [ {
        ptype : 'rowexpander',
        rowBodyTpl : [  '<div class="search-item"><p><h4>Sparql Query :</h4> {sparqlQuery}</p>\n\
                                   <p><h4>MappingDescription :</h4> {mappingDescription}</p></div>'
        ]
    },
    {
        ptype: 'cellediting',
        clicksToEdit: 1
    }
    ],
    selModel: {
        selType: 'cellmodel'
    },
    columns:[
        
//    {      
//        text: "No",
//        dataIndex: 'id',
//        width: 30
//        
//    },    
    {      
        text: "Results",
        dataIndex: 'sparqlQuery',
        flex: 1,
        xtype: 'templatecolumn',
        renderer: function (value) {
            return '<a href="#">GAGA'+value+'</a>';
        },

        tpl: new Ext.XTemplate('<tpl for=".">',
            '<h3><a>{descriptiveSentence}</a></h3></tpl>')
    },{
        text: "Relevance Mark",
        dataIndex: 'relevanceMark',
        width: 120,
        align: 'left',
        sortable: false,
        xtype: 'templatecolumn',
        tpl: new Ext.XTemplate( 
            '<div>',
            '<h3><span>{relevanceMark}</span></h3>',
            '</div>'
            )
    },
    {
        xtype: 'checkcolumn',
        header: 'Validate?',
        dataIndex: 'validate',
        width: 55,
        listeners: {
            checkchange: function() {

                console.log('Checked in the Grid');
            } 
        }
    }],
    dockedItems: [
    {
        xtype: 'toolbar',
        dock: 'top',
        items: [{
            xtype: 'tbtext',
            text: '<div id="mapping-info"><b>What do you mean?</b></br>Double click on the row to execute a SPARQL query for a selected item</div>'
        }
        ]
    },
    {
        dock: 'bottom',
        xtype:'searchpagingtoolbar'     
    }],
    initComponent: function() {
        this.callParent(arguments);
    }
});

































