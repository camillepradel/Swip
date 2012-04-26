Ext.define('SWIP.view.Viewer', {
    extend: 'Ext.tab.Panel',
    alias: 'widget.viewer',  
    requires: [
    'SWIP.view.search.MappingGrid',
    'SWIP.view.search.MatchingGrid',
    'Ext.ux.TabScrollerMenu', 
    'Ext.ux.TabCloseMenu',
    'Ext.panel.Panel'
    ],
    activeTab:       0,
    layout: 'fit',
    resizeTabs: true,
    enableTabScroll: true,
    plugins: [{
        ptype: 'tabscrollermenu',
        maxText  : 15,
        pageSize : 5,
        truncateLength: 20
    }],
    
    initComponent: function() {
        this.items = [{
            title: 'Pivot Query',
            html: 'Pivot Query'
        },{
            title: 'Matching',
            xtype: 'matchingrid'
        },{
            title: 'Mappings',
            xtype: 'mappingrid',
            region: 'center'
         
        }];   
        this.callParent(arguments);
    }   
});


