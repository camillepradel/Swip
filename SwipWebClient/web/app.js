Ext.Loader.setConfig({
        enabled : true,
         paths   : {
            Swip : 'web'
        } 
    });

Ext.application({
    name: 'SWIP',
    autoCreateViewport: true,
    paths: {
        'Ext.ux': 'ext4/examples/ux/' 
    },
    
    controllers: [
        'SwipController'
    ]
    

});
