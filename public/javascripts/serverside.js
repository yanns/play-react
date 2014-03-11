var React = require('./react'),
    CommentBox = require('./CommentBox');

// take data from parameters
var data = JSON.parse(process.argv[2]);

var backend = {
    loadCommentsFromServer: function(settings) {
    },
    handleCommentSubmit: function(settings) {
    }
};

console.log(React.renderComponentToString(CommentBox(backend)({data: data, onServerSide: true})));


