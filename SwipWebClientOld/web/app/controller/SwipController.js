var model;

Ext.define('SWIP.controller.SwipController', {
    extend: 'Ext.app.Controller',
    hasSearch : false,

    stores: ['Mappings','HistoryRecords','Matchings' ],

    models: ['Mapping','HistoryRecord','Matching'],

    views: ['search.MappingGrid','search.SearchPagingToolbar', 'search.SearchBar','Viewer','search.MatchingGrid', 'Configuration'],

    refs: [{
        ref: 'searchbar',
        selector: 'searchbar'
    },
    {
        ref: 'mappingrid',
        selector: 'mappingrid'
    },
    {
        ref: 'matchingrid',
        selector: 'matchingrid'
    },
    {
        ref: 'searchpagingtoolbar',
        selector: 'searchpagingtoolbar'
    },

    {
        ref: 'viewer',
        selector: 'viewer'
    },
    {
        ref: 'configuration',
        selector: 'configuration'
    }
    ],



    init: function() {
        this.control({
            'mappingrid': {
                itemdblclick: this.viewSearchResult
            },
            'mappingrid checkcolumn': {
                checkchange: this.editResult
            },
          
            'searchbar':{
                afterrender: this.onSearchBarAfterRender,
                change: this.onSearchBarChange,
                trigger1click: this.onSearchBarTrigger1Click,
                trigger2click: this.onSearchBarTrigger2Click
            },
            'searchpagingtoolbar button[action=validate]': {
                click: this.validateResult
            }
        });
    },

    viewSearchResult: function(mappingrid, record) {
        var viewer = this.getViewer();
        Ext.Function.defer(function () {
            viewer.add({
                title: record.get('descriptiveSentence'),
                iconCls: 'tabs-icon',
                html: record.get('sparqlQuery'),
                closable: true
            });
        }, 100);


    },

    onSearchBarAfterRender: function() {
        var comboSearchBar = this.getSearchbar();
        comboSearchBar.store = this.getMappingsStore();
        comboSearchBar.triggerEl.item(0).setDisplayed('none');
        comboSearchBar.store   = this.getHistoryRecordsStore();
    },

    onSearchBarChange: function(){
        var comboSearchBar = this.getSearchbar();
        comboSearchBar.store   = this.getHistoryRecordsStore();
        comboSearchBar.store.load();
    },

    onSearchBarTrigger1Click : function(){
        var me = this.getSearchbar(),
        store = this.getMappingsStore(),
        proxy = store.getProxy(),
        val;
        var check=this.getConfigurationValue(); 
        if (me.hasSearch) {
            me.setValue('');
            
            if (check == 1){
                proxy.extraParams[me.paramNames['plquery']] = '';
                store.load();
            }
            else if (check == 2){
                this.getMatchingsStore().getProxy().extraParams[me.paramNames['mquery']] = '';
                this.getMatchingsStore().load();
                proxy.extraParams[me.paramNames['plquery']] = '';
                store.load();
            } else 
            if (check == 0 || check == 3){
                proxy.extraParams[me.paramNames['plquery']] = '';
                store.load();
                this.getMatchingsStore().getProxy().extraParams[me.paramNames['mquery']] = '';
                this.getMatchingsStore().load();
            }    
                
            me.hasSearch = false;
            me.triggerEl.item(0).setDisplayed('none');
            me.doComponentLayout();
        }
    },

    onSearchBarTrigger2Click : function(){
        var recordIndex = -2;
        var comboSearchBar = this.getSearchbar();

        var me = this.getSearchbar(),
        store = this.getMappingsStore(),
        proxy = store.getProxy(),
        value = me.getValue();

        if (value.length < 1) {
            this.onSearchBarTrigger1Click();
            return;
        }

        var storeMatching = this.getMatchingsStore();
        var proxyMatching = storeMatching.getProxy();
        var check=this.getConfigurationValue();

        switch (check)
        {
            case 0:
                console.log('param name is '+me.paramNames['plquery']);
                proxy.extraParams[me.paramNames['plquery']] = value;
                store.load();
                console.log('param name is '+me.paramNames['mquery']);
                proxyMatching.extraParams[me.paramNames['mquery']] = value;
                this.getMatchingsStore().load();
                this.getViewer().setActiveTab(2);
                break;
            case 1:
                console.log('param name is '+me.paramNames['plquery']);
                proxy.extraParams[me.paramNames['plquery']] = value;
                store.load();
                this.getViewer().setActiveTab(2);
                break;
            case 2:
                console.log('param name is '+me.paramNames['mquery']);
                proxyMatching.extraParams[me.paramNames['mquery']] = value;
                this.getMatchingsStore().load();
                this.getViewer().setActiveTab(1);
                break;
            default:
                this.getViewer().setActiveTab(0);
        }

        me.hasSearch = true;
        me.triggerEl.item(0).setDisplayed('block');
        me.doComponentLayout();


        // History
        comboSearchBar.store = this.getHistoryRecordsStore();

        recordIndex = comboSearchBar.store.findBy(
            function(record, id){
                console.log('record get query : ' +record.get('query'));
                if(record.get('query') == value){
                    return true;  // a record with this data exists
                }
                return false;  // there is no record in the store with this data
            }
            );

        if (recordIndex > -1){
            model =  comboSearchBar.store.getAt(recordIndex);
            console.log('Query : '+ model.get('query') + ' '+ 'searchDate = '+model.get('searchDate')+' Index = '+ recordIndex);
        }
        else if (recordIndex == -1)
        {

            console.log(' New model and value'+value);
            console.log(new Date());
            model = Ext.ModelMgr.create({
                query: value,
                searchDate: new Date()
            }, 'SWIP.model.HistoryRecord');
            comboSearchBar.store.add(model);
            comboSearchBar.store.sync();
        }
    },


    getConfigurationValue: function(){

        var checkbox1 = this.getConfiguration().down('fieldcontainer').getComponent('checkbox1');
        console.log('Mappings  '+checkbox1.getValue());
        var valCheckBox1 = checkbox1.getValue();

        var checkbox2 = this.getConfiguration().down('fieldcontainer').getComponent('checkbox2');
        console.log('Matchings '+checkbox2.getValue());
        var valCheckBox2 = checkbox2.getValue();

        if (valCheckBox1 && valCheckBox2){
            console.log('Mapping+Matching');
            return 0;
        }
        if (valCheckBox1 && !valCheckBox2){
            console.log('Mappings checked');
            return 1;
        }
        if (!valCheckBox1 && valCheckBox2){
            console.log('Matchings checked');
            return 2;
        }

        if (!valCheckBox1 && !valCheckBox2){
            console.log('Nothing checked');
            return 3;
        }
    },
  
  
    validateResult: function(){ 
    },
    editResult: function(column, recordIndex, checked){
         
        console.log('validate? ' +recordIndex+ '  '+checked);
        console.log('validate value ' + this.getMappingsStore().getAt(recordIndex).get('validate'));
        model =  this.getMappingsStore().getAt(recordIndex);
  }
});
