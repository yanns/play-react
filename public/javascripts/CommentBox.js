var React = require('./react'),
    CommentList = require('./CommentList'),
    CommentForm = require('./CommentForm');

var CommentBox = function(backend) {
    return React.createClass({
        getInitialState: function() {
            return {data: this.props.data || []};
        },
        handleCommentSubmit: function(comment) {
            backend.handleCommentSubmit({
                data: JSON.stringify(comment),
                success: function(data) {
                    this.setState({data: data});
                }.bind(this)
            });
        },
        componentWillMount: function() {
            backend.loadCommentsFromServer({
                success: function(data) {
                    this.setState({data: data});
                }.bind(this),
                error: function(xhr, status, err) {
                    console.error(status, err.toString());
                }.bind(this)
            });
        },
        render: function() {
            return (
                React.DOM.div({
                    id: 'commentBox',
                    className: 'commentBox',
                    children: [
                        React.DOM.h1({children: 'Comments'}),
                        CommentList({data: this.state.data}),
                        CommentForm({onCommentSubmit: this.handleCommentSubmit})
                    ]
                })
            );
        }
    });
};

module.exports = CommentBox;