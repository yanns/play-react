var React = require('./react');
var Comment = require('./Comment');

var CommentList = React.createClass({
    render: function() {
        var commentsNodes = this.props.data.map(function(comment) {
            return Comment({author: comment.author, children: comment.text})
        });
        return (
            React.DOM.div({
                className: 'commentList',
                children: commentsNodes
            })
        );
    }
});

module.exports = CommentList;
