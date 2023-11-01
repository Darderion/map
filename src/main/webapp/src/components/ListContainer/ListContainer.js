import React from "react";
import { Title } from "@mantine/core";
import './ListContainer.css'

const ListPage = ({ title, buttons, search, list }) => {
    return (
        <div className="pageContainer">
            <div className="listPage">
                <div className="listHead">
                    <Title className="listName" order={1}>{title}</Title>
                    <div className="listButtons">
                        {buttons.map(b => b)}
                    </div>
                </div>
                <div className="search">
                    {search}
                </div>
                {list}
            </div>
        </div>
    )
}

export default ListPage;
