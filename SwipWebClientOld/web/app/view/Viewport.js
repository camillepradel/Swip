Ext.define('SWIP.view.Viewport', {
    extend: 'Ext.container.Viewport',
    requires: [
    'SWIP.view.Viewer',
    'SWIP.view.Header',
    'SWIP.view.Options',
    'SWIP.view.search.SearchBar',
    'Ext.layout.container.Border'
    ],
    layout: {
        type: 'border',
        align : 'stretch',
        pack: 'start',
        padding: 5
    },
    defaults: {
        split: true
    },
    
    items: [
    {
        xtype: 'swipheader',
        region: 'north'
    },
    {
        region: 'west',
        width: 225,
        xtype: 'swipoptions'
    },
    {
        region: 'center',
        xtype: 'panel',
        layout : {
            type  : 'fit',
            align : 'stretch',
            pack: 'start'
        },
        dockedItems: [{
            xtype: 'searchbar',
            dock: 'top'
        }],
        items: [{
            xtype: 'viewer'
        }]  
    }
    ]   
});
