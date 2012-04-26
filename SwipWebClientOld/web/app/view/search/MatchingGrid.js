Ext.Loader.setConfig({
    enabled: true
});

Ext.Loader.setPath('Ext.ux', 'ext4/examples/ux/');
Ext.tip.QuickTipManager.init();
Ext.define('SWIP.view.search.MatchingGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.matchingrid',
    requires: [
    'Ext.grid.*',
    'Ext.data.*',
    'Ext.util.*',
    'Ext.tip.ToolTip',
    'Ext.layout.container.Fit',   
    'Ext.toolbar.Paging',
    'SWIP.view.feature.Selectable'
    ],
    store: 'Matchings',
    cls: 'search-results',
    autoScroll: true,
    layout : {
        type  : 'fit',
        align : 'stretch',
        pack: 'start'
    },
    features: [
    {
        ftype: 'selectable',
        id: 'selectable'
    },
    {
        ftype: 'grouping',
        startCollapsed : true,
        groupHeaderTpl: '<h3>Keyword : {name} </h3>'
    }
    ],
    dockedItems: [
    {
        dock: 'bottom',
        xtype:'pagingtoolbar',
        store: 'Matchings' 
    }] ,
    columns:[
    {  
        text: "Results",
        dataIndex: 'matchingType',
        flex: 1,
        xtype: 'templatecolumn',
        tpl: 
            new Ext.XTemplate(
        '<tpl for=".">',
            '<tpl if="matchingType == \'class\'">',
                '<div id="matching-class" data-qtitle="Matching Type" data-qwidth="100" data-qtip="It is a class!"><h3><a>{uri}</a></h3>',
                    '<div class="search-item">',
                        '<h4>matchingQuerried :</h4> {matchingQuerried}',
                        '<h4>MatchedLabel :</h4> {matchingMatchedLabel}',
                    '</div>',
                  '</div>',
            '</tpl>',      
            '<tpl if="matchingType == \'property\'">',
                '<div id="matching-property" data-qtitle="Matching Type" data-qwidth="100" data-qtip="It is a property!"><h3><a>{uri}</a></h3>',
                    '<div class="search-item">',
                        '<h4>matchingQuerried :</h4> {matchingQuerried}',
                        '<h4>MatchedLabel :</h4> {matchingMatchedLabel}',
                    '</div>',
                '</div>',
            '</tpl>',    
            '<tpl if="matchingType == \'individual\'">',
                '<div id="matching-instance" data-qtitle="Matching Type" data-qwidth="100" data-qtip="It is an individual!"><h3><a>{uri}</a></h3>',
                    '<div class="search-item">',
                        '<h4>matchingQuerried :</h4> {matchingQuerried}',
                        '<h4>MatchedLabel :</h4> {matchingMatchedLabel}',
                     '</div>',
                '</div>',
            '</tpl>',    
            '</tpl>'          
            ) 
        }, {
        text: "Matching Trust Mark",
        dataIndex: 'matchingTrustMark',
        width: 120,
        align: 'left',
        sortable: true,
        xtype: 'templatecolumn',
        tpl: new Ext.XTemplate( 
            '<div>',
            '<h3><span>{matchingTrustMark}</span></h3>',
            '</div>'
            )
    }],
    
    initComponent: function() {
        this.callParent(arguments);
    }
});






















